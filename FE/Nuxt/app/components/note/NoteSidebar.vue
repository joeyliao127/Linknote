<template>
    <div>
        <!-- Overlay backdrop -->
        <div
            :class="[
                'fixed inset-0 bg-black/50 z-40 transition-opacity duration-300',
                isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none',
            ]"
            @click="emit('close')" />

        <!-- Sidebar panel -->
        <aside
            :class="[
                'fixed left-0 top-0 h-screen w-72 bg-[#1a1d1a] border-r border-slate-700/50 z-50',
                'flex flex-col overflow-hidden',
                'transition-transform duration-300',
                isOpen ? 'translate-x-0' : '-translate-x-full',
            ]">
            <!-- Top bar: close button + LinkNote title -->
            <div
                class="h-12 flex items-center justify-between px-3 border-b border-slate-700/50 shrink-0">
                <UButton
                    variant="ghost"
                    icon="i-lucide-menu"
                    size="sm"
                    class="text-slate-400 hover:text-white"
                    @click="emit('close')" />
                <button
                    class="text-2xl font-bold text-primary hover:text-accent/80 transition-colors"
                    @click="navigateTo('/notebooks')">
                    LinkNote
                </button>
                <!-- spacer to center the title -->
                <div class="w-8" />
            </div>

            <!-- Search bar -->
            <div class="px-3 py-2 border-b border-slate-700/50 shrink-0">
                <UInput
                    v-model="searchKeyword"
                    placeholder="Search note"
                    size="sm"
                    class="w-full"
                    :trailing-icon="'i-lucide-search'" />
            </div>

            <!-- Notebook row: name + actions -->
            <div
                class="flex items-center gap-1.5 px-3 py-2 border-b border-slate-700/50 shrink-0">
                <span
                    class="flex-1 text-sm font-semibold text-slate-200 truncate min-w-0">
                    {{ notebookName || "..." }}
                </span>

                <!-- New note button -->
                <UButton
                    size="xs"
                    color="primary"
                    class="shrink-0 text-white font-semibold"
                    @click="handleCreateNote">
                    New
                </UButton>

                <!-- Filter popover -->
                <UPopover>
                    <UButton
                        size="xs"
                        variant="outline"
                        icon="i-lucide-filter"
                        class="shrink-0 text-slate-400" />
                    <template #content>
                        <NoteFilterPopover
                            :star-only="starOnly"
                            :sort-order="sortOrder"
                            @update:star-only="starOnly = $event"
                            @update:sort-order="sortOrder = $event" />
                    </template>
                </UPopover>

                <!-- Tag filter popover -->
                <UPopover>
                    <UButton
                        size="xs"
                        variant="outline"
                        icon="i-lucide-tag"
                        :class="[
                            'shrink-0',
                            selectedTagIds.length > 0
                                ? 'text-accent border-accent/50'
                                : 'text-slate-400',
                        ]" />
                    <template #content>
                        <TagForm
                            :tags="allTags"
                            :selected-tag-ids="selectedTagIds"
                            @update:selected="selectedTagIds = $event"
                            @create="handleCreateTag"
                            @delete="handleDeleteTag" />
                    </template>
                </UPopover>
            </div>

            <!-- Notes list (infinite scroll) -->
            <div ref="listRef" class="flex-1 min-h-0 overflow-y-auto">
                <div class="py-1 px-0.5">
                    <button
                        v-for="note in notes"
                        :key="note.id"
                        :class="[
                            'w-full flex items-center justify-between pl-3 pr-1 py-2 text-sm transition-colors',
                            note.id === currentNoteId
                                ? 'bg-white/[0.08] text-white'
                                : 'text-slate-400 hover:bg-white/[0.05] hover:text-slate-200',
                        ]"
                        @click="handleNoteSelect(note.id)">
                        <span class="flex-1 truncate text-left">
                            {{ note.title || "(Untitled)" }}
                        </span>
                        <StarButton
                            :id="note.id"
                            :stared="note.star ?? false"
                            @change="handleStarToggle" />
                    </button>

                    <!-- Empty state -->
                    <p
                        v-if="!isLoading && notes.length === 0"
                        class="text-center text-xs text-slate-600 italic py-6">
                        No notes found
                    </p>

                    <!-- Loading spinner -->
                    <div v-if="isLoading" class="flex justify-center py-3">
                        <UIcon
                            name="i-lucide-loader-2"
                            class="w-4 h-4 animate-spin text-slate-500" />
                    </div>

                    <!-- Infinite scroll sentinel -->
                    <div ref="sentinelRef" class="h-1" />
                </div>
            </div>

            <!-- Bottom: email + signout (same as SidebarV2) -->
            <div
                class="px-3 py-3 flex flex-col gap-2 border-t border-slate-700/50 shrink-0">
                <div
                    class="flex items-center gap-2 px-3 py-2 bg-white/5 rounded text-xs text-slate-400">
                    <span class="w-2 h-2 rounded-full bg-primary shrink-0" />
                    <span class="truncate">{{ userEmail }}</span>
                </div>
                <UButton
                    block
                    color="primary"
                    icon="i-lucide-log-out"
                    class="text-white font-semibold"
                    @click="handleSignOut">
                    SignOut
                </UButton>
            </div>
        </aside>
    </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onBeforeUnmount } from "vue";
import { useAuth, navigateTo } from "#imports";
import type { Note } from "~~/types/Note";
import type { Tag } from "~~/types/Tag";
import { useNote } from "~/composables/model/useNote";
import { useNotebook } from "~/composables/model/useNotebook";
import { useTag } from "~/composables/model/useTag";
import TagForm from "./TagForm.vue";
import StarButton from "~/components/ui/StarButton.vue";
import NoteFilterPopover from "./NoteFilterPopover.vue";

const props = defineProps<{
    isOpen: boolean;
    notebookId: string;
    currentNoteId: string;
    currentNoteTitle?: string;
}>();

const emit = defineEmits<{
    close: [];
    noteSelect: [noteId: string];
}>();

// ── Auth ──────────────────────────────────────────────────────────────
const { data, signOut } = useAuth();
const userEmailFallback = computed(
    () => (data.value as any)?.user?.email ?? "",
);
const { data: userInfo } = useFetch<{
    userId: string;
    email: string;
    username: string;
}>("/api/user/token");
const userEmail = computed(
    () => userInfo.value?.email || userEmailFallback.value,
);

async function handleSignOut() {
    await signOut({ callbackUrl: "/signIn" });
}

// ── Composables ───────────────────────────────────────────────────────
const { indexNotes, createNote, updateNote } = useNote();
const { getNotebook } = useNotebook();
const { indexTags, createTag, deleteTag } = useTag();

// ── State ─────────────────────────────────────────────────────────────
const notebookName = ref("");
const notes = shallowRef<Note[]>([]);
const page = ref(1);
const hasMore = ref(true);
const isLoading = ref(false);
const searchKeyword = ref("");
const debouncedSearch = ref("");
const starOnly = ref(false);
const sortOrder = ref<"asc" | "desc">("desc");
const selectedTagIds = ref<string[]>([]);
const allTags = shallowRef<Tag[]>([]);
const initialized = ref(false);

const listRef = ref<HTMLElement | null>(null);
const sentinelRef = ref<HTMLElement | null>(null);

// ── Search debounce ───────────────────────────────────────────────────
let searchTimer: ReturnType<typeof setTimeout> | null = null;
watch(searchKeyword, (val) => {
    if (searchTimer) clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
        debouncedSearch.value = val;
    }, 400);
});

// ── Data loading ──────────────────────────────────────────────────────
async function fetchNotebookInfo() {
    if (!props.notebookId) return;
    try {
        const nb = await getNotebook(props.notebookId);
        notebookName.value = nb.title || "";
    } catch {
        notebookName.value = "";
    }
}

async function fetchAllTags() {
    try {
        const res = await indexTags();
        allTags.value = (res.items as unknown as Tag[]) ?? [];
    } catch {
        allTags.value = [];
    }
}

async function loadNotes(reset = false) {
    if (isLoading.value) return;
    if (!hasMore.value && !reset) return;
    if (!props.notebookId) return;

    if (reset) {
        page.value = 1;
        notes.value = [];
        hasMore.value = true;
    }

    isLoading.value = true;
    try {
        const result = await indexNotes(props.notebookId, {
            page: page.value,
            pageSize: 20,
            title: debouncedSearch.value || undefined,
            star: starOnly.value || undefined,
            sort: sortOrder.value,
            tagIds: selectedTagIds.value.length
                ? selectedTagIds.value
                : undefined,
        });
        notes.value = [...notes.value, ...result.items];
        hasMore.value = page.value < result.totalPage;
        page.value++;
    } catch {
        // silently handle
    } finally {
        isLoading.value = false;
    }
}

function resetAndLoad() {
    loadNotes(true);
}

// Initial load on first open
watch(
    [() => props.isOpen, () => props.notebookId] as const,
    async ([open, nbId]: [boolean, string]) => {
        if (open && nbId && !initialized.value) {
            await Promise.all([
                fetchNotebookInfo(),
                loadNotes(true),
                fetchAllTags(),
            ]);
            initialized.value = true;
        }
    },
    { immediate: true },
);

// Sync current note title edits into the local notes list
watch(
    () => props.currentNoteTitle,
    (title) => {
        if (!title || !props.currentNoteId) return;
        const idx = notes.value.findIndex((n) => n.id === props.currentNoteId);
        if (idx !== -1 && notes.value[idx].title !== title) {
            notes.value = notes.value.map((n, i) =>
                i === idx ? { ...n, title } : n,
            ) as Note[];
        }
    },
);

// Reset when notebookId changes
watch(
    () => props.notebookId,
    (newId, oldId) => {
        if (newId !== oldId) {
            initialized.value = false;
        }
    },
);

// Reload when filters change
watch([debouncedSearch, starOnly, sortOrder, selectedTagIds], () => {
    if (initialized.value) resetAndLoad();
});

// ── Infinite scroll ───────────────────────────────────────────────────
let observer: IntersectionObserver | null = null;

function setupObserver() {
    observer?.disconnect();
    if (!sentinelRef.value) return;
    observer = new IntersectionObserver(
        (entries) => {
            const entry = entries[0];
            if (entry?.isIntersecting && !isLoading.value && hasMore.value) {
                loadNotes();
            }
        },
        { threshold: 0.1 },
    );
    observer.observe(sentinelRef.value);
}

watch(sentinelRef, (el) => {
    if (el) setupObserver();
});

onBeforeUnmount(() => observer?.disconnect());

// ── Note actions ──────────────────────────────────────────────────────
function handleNoteSelect(noteId: string) {
    emit("noteSelect", noteId);
    emit("close");
}

async function handleCreateNote() {
    if (!props.notebookId) return;
    try {
        const note = await createNote({
            notebookId: props.notebookId,
            title: "Untitled",
        });
        notes.value = [note, ...notes.value];
        emit("noteSelect", note.id);
        emit("close");
    } catch {
        // silently handle
    }
}

async function handleStarToggle(value: { id: string; stared: boolean }) {
    const idx = notes.value.findIndex((n) => n.id === value.id);
    if (idx === -1) return;
    const original = notes.value[idx] as Note;
    const updated: Note = { ...original, star: value.stared };
    notes.value = notes.value.map((n, i) =>
        i === idx ? updated : n,
    ) as Note[];
    try {
        await updateNote(updated);
    } catch {
        const reverted: Note = { ...updated, star: !value.stared };
        notes.value = notes.value.map((n, i) =>
            i === idx ? reverted : n,
        ) as Note[];
    }
}

// ── Tag actions ───────────────────────────────────────────────────────
async function handleCreateTag(name: string) {
    try {
        const tag = await createTag(name);
        allTags.value = [...allTags.value, tag];
    } catch {
        // silently handle
    }
}

async function handleDeleteTag(tagId: string) {
    try {
        await deleteTag(tagId);
        allTags.value = allTags.value.filter((t) => t.id !== tagId);
        selectedTagIds.value = selectedTagIds.value.filter(
            (id) => id !== tagId,
        );
    } catch {
        // silently handle
    }
}
</script>

<template>
    <NoteShell>
        <template #sidebar>
            <NoteSidebar
                :is-open="isSidebarOpen"
                :notebook-id="currentNote?.notebookId ?? ''"
                :current-note-id="currentNote?.id ?? ''"
                :current-note-title="currentNote?.title"
                @close="isSidebarOpen = false"
                @note-select="router.push(`/notes/${$event}`)" />
        </template>

        <template #header>
            <div
                class="flex items-center h-12 px-2 gap-x-1 border-b border-slate-700/50 bg-[var(--background)]">
                <!-- Left: sidebar toggle -->
                <UButton
                    variant="ghost"
                    icon="i-lucide-menu"
                    size="sm"
                    class="text-slate-400 hover:text-white shrink-0"
                    @click="isSidebarOpen = true" />

                <!-- Center: breadcrumb + title + save status -->
                <div
                    class="flex-1 flex justify-center items-center gap-x-2 overflow-hidden px-1 min-w-0">
                    <span
                        class="text-xs text-slate-500 shrink-0 hidden sm:block">
                        {{ notebookName }}
                    </span>
                    <span
                        class="text-xs text-slate-600 shrink-0 hidden sm:block">
                        ›
                    </span>
                    <FormTitleEditor
                        v-if="currentNote"
                        v-model="currentNote.title"
                        @change="scheduleSave" />
                    <span
                        v-if="saveStatus === 'saving'"
                        class="text-xs text-slate-500 shrink-0">
                        Saving...
                    </span>
                    <span
                        v-else-if="saveStatus === 'saved'"
                        class="text-xs text-emerald-500 shrink-0">
                        Saved ✓
                    </span>
                </div>

                <!-- Right: action buttons -->
                <div class="flex items-center gap-x-0.5 shrink-0">
                    <!-- Tag popover -->
                    <UPopover v-if="currentNote">
                        <UTooltip text="Add tags">
                            <UButton
                                variant="ghost"
                                icon="i-lucide-tag"
                                size="sm"
                                class="text-slate-400 hover:text-white" />
                        </UTooltip>
                        <template #content>
                            <TagForm
                                :tags="allTags"
                                :selected-tag-ids="currentNote.tagIdList ?? []"
                                @update:selected="handleTagsUpdate"
                                @create="handleTagCreate"
                                @delete="handleTagDelete" />
                        </template>
                    </UPopover>

                    <!-- Layout toggle -->
                    <UTooltip
                        :text="
                            editorMode === 'single'
                                ? 'Dual editor'
                                : 'Single editor'
                        ">
                        <UButton
                            variant="ghost"
                            :icon="
                                editorMode === 'single'
                                    ? 'i-lucide-columns-2'
                                    : 'i-lucide-square'
                            "
                            size="sm"
                            :class="[
                                editorMode === 'dual'
                                    ? 'text-accent'
                                    : 'text-slate-400 hover:text-white',
                            ]"
                            @click="
                                editorMode =
                                    editorMode === 'single' ? 'dual' : 'single'
                            " />
                    </UTooltip>

                    <!-- TOC toggle -->
                    <UTooltip text="Table of contents">
                        <UButton
                            variant="ghost"
                            icon="i-lucide-list"
                            size="sm"
                            :class="[
                                isTocOpen
                                    ? 'text-accent'
                                    : 'text-slate-400 hover:text-white',
                            ]"
                            @click="isTocOpen = !isTocOpen" />
                    </UTooltip>

                    <!-- Delete note -->
                    <UTooltip text="Delete note">
                        <UButton
                            variant="ghost"
                            icon="i-lucide-trash-2"
                            size="sm"
                            class="text-slate-400 hover:text-red-400"
                            @click="handleDelete" />
                    </UTooltip>
                </div>
            </div>
        </template>

        <template #main>
            <div v-if="currentNote" class="flex h-full overflow-hidden">
                <!-- TOC nav column (collapsible) -->
                <Transition name="toc-slide">
                    <aside
                        v-if="isTocOpen"
                        class="w-40 shrink-0 border-r border-slate-700/60 overflow-y-auto bg-black/40 backdrop-blur-sm">
                        <NoteHeadingNav
                            :headings="contentHeadings"
                            :editor-el="contentEditorElWrapper" />
                    </aside>
                </Transition>

                <!-- Main content column -->
                <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
                    <!-- Question section (collapsible) -->
                    <div class="border-b border-slate-700/50 shrink-0">
                        <button
                            class="w-full flex items-center gap-2 px-4 py-2 text-xs text-slate-400 hover:text-slate-200 hover:bg-white/[0.06] transition-colors bg-black/40 backdrop-blur-sm"
                            @click="isQuestionOpen = !isQuestionOpen">
                            <UIcon
                                :name="
                                    isQuestionOpen
                                        ? 'i-lucide-chevron-down'
                                        : 'i-lucide-chevron-right'
                                "
                                class="w-3.5 h-3.5 shrink-0" />
                            <span class="font-medium">Question</span>
                            <span class="text-slate-600 text-xs">
                                Why are we writing this note?
                            </span>
                        </button>
                        <Transition name="question-expand">
                            <div
                                v-if="isQuestionOpen"
                                class="border-t border-slate-700/50 h-40">
                                <TiptapEditor
                                    v-model="currentNote.question"
                                    placeholder="Why are we writing this note..."
                                    @update:model-value="scheduleSave" />
                            </div>
                        </Transition>
                    </div>

                    <!-- Editor row -->
                    <div class="flex flex-1 min-h-0 overflow-hidden">
                        <!-- Content editor -->
                        <div
                            ref="contentEditorElWrapper"
                            class="flex-1 min-w-0 overflow-hidden flex flex-col">
                            <div
                                class="px-4 py-1.5 text-xs font-medium text-slate-400 border-b border-slate-700/50 bg-black/50 backdrop-blur-sm shrink-0">
                                Content / Capture
                            </div>
                            <TiptapEditor
                                v-model="currentNote.content"
                                placeholder="Start writing..."
                                @update:model-value="scheduleSave"
                                @headings="contentHeadings = $event" />
                        </div>

                        <!-- Keypoint panel (dual mode) -->
                        <Transition name="keypoint-slide">
                            <div
                                v-if="editorMode === 'dual'"
                                class="w-1/2 shrink-0 border-l border-slate-700/50 flex flex-col overflow-hidden">
                                <div
                                    class="px-4 py-1.5 text-xs font-medium text-slate-400 border-b border-slate-700/50 bg-black/50 backdrop-blur-sm shrink-0">
                                    Distill / Keypoints
                                </div>
                                <TiptapEditor
                                    v-model="currentNote.keypoint"
                                    placeholder="Key takeaways... (Distill)"
                                    @update:model-value="scheduleSave" />
                            </div>
                        </Transition>
                    </div>
                </div>
            </div>

            <!-- Loading state -->
            <div
                v-else
                class="flex h-full items-center justify-center bg-black/40">
                <UIcon
                    name="i-lucide-loader-2"
                    class="w-6 h-6 animate-spin text-slate-500" />
            </div>
        </template>
    </NoteShell>
</template>

<script setup lang="ts">
definePageMeta({ layout: "main" });

import { ref, onMounted } from "vue";
import { NoteShell } from "#components";
import NoteSidebar from "~/components/note/NoteSidebar.vue";
import TiptapEditor from "~/components/note/TiptapEditor.vue";
import NoteHeadingNav from "~/components/note/NoteHeadingNav.vue";
import TagForm from "~/components/note/TagForm.vue";
import FormTitleEditor from "~/components/form/FormTitleEditor.vue";
import type { HeadingItem } from "~/components/note/TiptapEditor.vue";
import type { Note } from "~~/types/Note";
import type { Tag } from "~~/types/Tag";
import { useNote } from "~/composables/model/useNote";
import { useNotebook } from "~/composables/model/useNotebook";
import { useTag } from "~/composables/model/useTag";

const route = useRoute();
const router = useRouter();
const dialog = useDialogs();

const { getNote, updateNote, deleteNote, addTags } = useNote();
const { getNotebook } = useNotebook();
const { indexTags, createTag, deleteTag } = useTag();

// ── State ──────────────────────────────────────────────────────────────
const currentNote = ref<Note>();
const notebookName = ref("");
const isSidebarOpen = ref(false);
const editorMode = ref<"single" | "dual">("dual");
const isQuestionOpen = ref(false);
const isTocOpen = ref(false);
const saveStatus = ref<"idle" | "saving" | "saved">("idle");
const contentHeadings = ref<HeadingItem[]>([]);
const allTags = ref<Tag[]>([]);
const contentEditorElWrapper = useTemplateRef<HTMLElement>(
    "contentEditorElWrapper",
);

// ── Auto-save ─────────────────────────────────────────────────────────
let saveTimer: ReturnType<typeof setTimeout> | null = null;
let savedTimer: ReturnType<typeof setTimeout> | null = null;

function scheduleSave() {
    if (saveTimer) clearTimeout(saveTimer);
    saveStatus.value = "saving";
    saveTimer = setTimeout(async () => {
        if (!currentNote.value) return;
        try {
            await updateNote({ ...currentNote.value });
            saveStatus.value = "saved";
            if (savedTimer) clearTimeout(savedTimer);
            savedTimer = setTimeout(() => {
                saveStatus.value = "idle";
            }, 2000);
        } catch {
            saveStatus.value = "idle";
        }
    }, 1000);
}

// ── Delete ────────────────────────────────────────────────────────────
function handleDelete() {
    dialog.confirm(
        "確定要刪除這篇筆記嗎？此操作無法復原。",
        "刪除筆記",
        async () => {
            if (!currentNote.value) return;
            await deleteNote(currentNote.value.id);
            dialog.inform("筆記已成功刪除");
            router.push("/notebooks");
        },
    );
}

// ── Tag management ────────────────────────────────────────────────────
async function handleTagsUpdate(tagIds: string[]) {
    if (!currentNote.value) return;
    currentNote.value.tagIdList = tagIds;
    await addTags(currentNote.value.id, tagIds);
}

async function handleTagCreate(name: string) {
    const tag = await createTag(name);
    allTags.value.push(tag);
}

async function handleTagDelete(tagId: string) {
    await deleteTag(tagId);
    allTags.value = allTags.value.filter((t) => t.id !== tagId);
    if (currentNote.value) {
        currentNote.value.tagIdList = (
            currentNote.value.tagIdList ?? []
        ).filter((id) => id !== tagId);
    }
}

// ── Hotkeys ───────────────────────────────────────────────────────────
function handleHotkey(e: KeyboardEvent) {
    if (!e.metaKey || !e.shiftKey) return;
    if (e.key === "1") {
        e.preventDefault();
        isSidebarOpen.value = !isSidebarOpen.value;
    } else if (e.key === "2") {
        e.preventDefault();
        editorMode.value = editorMode.value === "dual" ? "single" : "dual";
    }
}

onMounted(() => window.addEventListener("keydown", handleHotkey));
onUnmounted(() => window.removeEventListener("keydown", handleHotkey));

// ── Init ──────────────────────────────────────────────────────────────
onMounted(async () => {
    const [note, tags] = await Promise.all([
        getNote(route.params.id as string),
        indexTags(),
    ]);
    note.tagIdList = note.tags.map((t) => t.id);
    currentNote.value = note;
    allTags.value = (tags.items as unknown as Tag[]) ?? [];

    if (note.notebookId) {
        const notebook = await getNotebook(note.notebookId);
        notebookName.value = notebook.title || "";
    }
});
</script>

<style scoped>
/* TOC panel slide in/out */
.toc-slide-enter-active,
.toc-slide-leave-active {
    transition:
        width 0.2s ease,
        opacity 0.15s ease;
    overflow: hidden;
}
.toc-slide-enter-from,
.toc-slide-leave-to {
    width: 0 !important;
    opacity: 0;
}

/* Question collapsible */
.question-expand-enter-active,
.question-expand-leave-active {
    transition:
        max-height 0.25s ease,
        opacity 0.2s ease;
    max-height: 200px;
    overflow: hidden;
}
.question-expand-enter-from,
.question-expand-leave-to {
    max-height: 0;
    opacity: 0;
}

/* Keypoint panel slide in/out */
.keypoint-slide-enter-active,
.keypoint-slide-leave-active {
    transition:
        width 0.22s ease,
        opacity 0.18s ease;
    overflow: hidden;
}
.keypoint-slide-enter-from,
.keypoint-slide-leave-to {
    width: 0 !important;
    opacity: 0;
}
</style>

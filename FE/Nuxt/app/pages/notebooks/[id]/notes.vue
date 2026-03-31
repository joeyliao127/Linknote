<template>
    <div class="flex flex-col h-full px-6 pt-6 gap-6">
        <!-- Header: Title + Toolbar -->
        <div class="flex items-center justify-between gap-6">
            <h1 class="text-3xl font-semibold text-white shrink-0">
                {{ notebook?.title || "Loading..." }}
            </h1>
            <NoteToolbar
                :tags="tags"
                :selected-tags="noteFilter.tagIdList"
                :keyword="noteFilter.keyword"
                :star-only="noteFilter.star"
                :sort-order="noteFilter.sort"
                :creating="creating"
                :deleting-notebook="deletingNotebook"
                @create="handleCreate"
                @reset="resetFilters"
                @toggle-star="toggleStarFilter"
                @change-tag="changeTag"
                @change-sort="changeSort"
                @search="searchKeyword"
                @delete-notebook="handleDeleteNotebook"
                @create-tag="handleCreateTag"
                @delete-tag="handleDeleteTag" />
        </div>

        <!-- Description Section -->
        <div
            v-if="!editingDesc"
            class="py-1 pl-2 rounded cursor-pointer transition-colors border border-transparent hover:bg-white/5 hover:border-accent/30"
            @click="startEditDesc">
            <p
                v-if="notebook?.description"
                class="text-sm text-slate-200/85 leading-relaxed">
                {{ notebook.description }}
            </p>
            <p v-else class="flex items-center gap-1 text-sm text-slate-200/55">
                <UIcon name="i-lucide-plus" class="w-4 h-4" />
                點擊新增描述
            </p>
        </div>
        <div v-else class="flex gap-3 items-center">
            <UInput
                v-model="descDraft"
                placeholder="輸入描述..."
                class="flex-1"
                :ui="{
                    base: 'bg-transparent border border-accent rounded text-slate-50/90 text-sm placeholder:text-slate-200/40 transition-colors',
                }" />
            <UButton size="sm" color="accent" variant="soft" @click="saveDesc">
                儲存
            </UButton>
            <UButton
                size="sm"
                color="secondary"
                variant="ghost"
                @click="cancelEditDesc">
                取消
            </UButton>
        </div>

        <!-- Notes Section Label -->
        <div
            class="text-sm font-semibold text-slate-200/75 uppercase tracking-[0.05em]">
            Notes
        </div>

        <!-- Notes List -->
        <div ref="scrollContainer" class="flex-1 overflow-y-auto min-h-0 pb-4">
            <div
                class="grid grid-cols-[repeat(auto-fill,minmax(320px,1fr))] gap-4">
                <template v-for="note in notes" :key="note.id">
                    <NoteCard
                        :id="note.id"
                        :title="note.title || '未命名筆記'"
                        :description="note.keypoint"
                        :tags="note.tags"
                        :starred="note.star"
                        :updated-at="note.updatedAt"
                        @toggle-star="toggleNoteStar"
                        @delete="deleteNoteItem"
                        @open="openNote" />
                </template>

                <!-- Load More Trigger -->
                <div
                    ref="loadMoreTrigger"
                    class="col-span-full flex items-center justify-center py-6 px-8 text-sm text-slate-400 rounded">
                    <UIcon
                        v-if="loadingMore"
                        name="i-lucide-loader-2"
                        class="w-5 h-5 animate-spin" />
                    <button
                        v-else-if="hasMore"
                        class="flex items-center gap-1 hover:text-accent"
                        @click="loadMore">
                        <UIcon name="i-lucide-chevron-down" class="w-4 h-4" />
                        載入更多
                    </button>
                    <span v-else>沒有更多筆記</span>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup lang="ts">
import {
    computed,
    onBeforeUnmount,
    onMounted,
    reactive,
    ref,
    watch,
} from "vue";
import { useRoute, useAuth, navigateTo } from "#imports";
import NoteToolbar from "~/components/note/NoteToolbar.vue";
import NoteCard from "~/components/ui/NoteCard.vue";
import ConfirmModal from "~/components/ui/ConfirmModal.vue";
import { useNote } from "~/composables/model/useNote";
import { useTag } from "~/composables/model/useTag";
import { useNotebook } from "~/composables/model/useNotebook";
import type { Note } from "~~/types/Note";
import type { Tag } from "~~/types/Tag";
import type { Notebook } from "~~/types/Notebook";

definePageMeta({ layout: "dashboard" });

const route = useRoute();
const auth = useAuth();

const { indexNotes, createNote, updateNote, deleteNote } = useNote();
const { indexTags, createTag, deleteTag } = useTag();
const { getNotebook, updateNotebook, deleteNotebook } = useNotebook();

// Notebook state
const notebook = ref<Notebook | null>(null);
const editingDesc = ref(false);
const descDraft = ref("");

// Notes and filters
const notes = ref<Note[]>([]);
const tags = ref<Tag[]>([]);
const noteFilter = reactive({
    keyword: "",
    tagIdList: [] as string[],
    star: false,
    sort: "desc" as "asc" | "desc",
});

// Loading and pagination
const creating = ref(false);
const deletingNotebook = ref(false);
const loading = ref(false);
const loadingMore = ref(false);
const page = ref(1);
const pageSize = 20;
const totalCount = ref(0);
const hasMore = computed(() => notes.value.length < totalCount.value);

// Refs
const loadMoreTrigger = ref<HTMLElement | null>(null);
const scrollContainer = ref<HTMLElement | null>(null);
let observer: IntersectionObserver | null = null;

// Computed
const userId = computed(() => auth.data.value?.user?.id || "");
const notebookId = computed(() => route.params.id as string);

// Load notebook details
async function loadNotebook() {
    try {
        const res = await getNotebook(notebookId.value);
        notebook.value = res;
    } catch (error) {
        console.error("Failed to load notebook:", error);
    }
}

// Fetch notes
async function fetchNotes(reset = false) {
    if (loading.value || loadingMore.value) return;
    if (reset) {
        page.value = 1;
        notes.value = [];
    }
    const isLoadMore = !reset && notes.value.length > 0;
    if (isLoadMore) loadingMore.value = true;
    else loading.value = true;

    try {
        const res = await indexNotes(notebookId.value, {
            page: page.value,
            pageSize,
            title: noteFilter.keyword || undefined,
            tagIdList:
                noteFilter.tagIdList && noteFilter.tagIdList.length > 0
                    ? noteFilter.tagIdList
                    : undefined,
            star: noteFilter.star || undefined,
            sort: noteFilter.sort,
        });
        const mapped = (res.items || []).map((n) => ({ ...n }));
        notes.value = reset ? mapped : [...notes.value, ...mapped];
        totalCount.value = res.count ?? notes.value.length;
        if (hasMore.value) page.value += 1;
    } finally {
        loading.value = false;
        loadingMore.value = false;
    }
}

// Fetch tags
async function fetchTags() {
    try {
        const res = await indexTags();
        tags.value = (res.items as Tag[]) || [];
    } catch (error) {
        console.error("Failed to load tags:", error);
    }
}

// Description editing
function startEditDesc() {
    descDraft.value = notebook.value?.description || "";
    editingDesc.value = true;
}

async function saveDesc() {
    if (!notebook.value) return;
    try {
        await updateNotebook(userId.value, notebookId.value, {
            ...notebook.value,
            description: descDraft.value,
        });
        notebook.value.description = descDraft.value;
        editingDesc.value = false;
    } catch (error) {
        console.error("Failed to save description:", error);
    }
}

function cancelEditDesc() {
    editingDesc.value = false;
}

// Note operations
async function handleCreate() {
    creating.value = true;
    try {
        const res = await createNote({
            title: "unknow",
            notebookId: notebookId.value,
            keypoint: "",
            content: "",
            star: false,
        });
        const newId = (res as any)?.id || (res as any)?.items?.[0]?.id;
        await fetchNotes(true);
        if (newId) {
            navigateTo(`/notes/${newId}`);
        }
    } catch (error) {
        console.error("Failed to create note:", error);
    } finally {
        creating.value = false;
    }
}

async function toggleNoteStar(id: string, next: boolean) {
    const target = notes.value.find((n) => n.id === id);
    if (!target) return;
    target.star = next;
    try {
        await updateNote({ ...target, notebookId: notebookId.value });
    } catch (error) {
        console.error("Failed to toggle star:", error);
    }
}

async function deleteNoteItem(id: string) {
    try {
        await deleteNote(id);
        notes.value = notes.value.filter((n) => n.id !== id);
        totalCount.value = Math.max(0, totalCount.value - 1);
    } catch (error) {
        console.error("Failed to delete note:", error);
    }
}

// Tag operations
async function handleCreateTag(name: string) {
    try {
        const newTag = await createTag(name);
        tags.value.push(newTag);
    } catch (error) {
        console.error("Failed to create tag:", error);
    }
}

async function handleDeleteTag(tagId: string) {
    try {
        await deleteTag(tagId);
        tags.value = tags.value.filter((t) => t.id !== tagId);
        // Remove from selected tags if it was selected
        if (noteFilter.tagIdList.includes(tagId)) {
            noteFilter.tagIdList = noteFilter.tagIdList.filter(
                (id) => id !== tagId,
            );
        }
    } catch (error) {
        console.error("Failed to delete tag:", error);
    }
}

// Delete notebook
async function handleDeleteNotebook() {
    deletingNotebook.value = true;
    try {
        await deleteNotebook(userId.value, notebookId.value);
        navigateTo("/notebooks");
    } catch (error) {
        console.error("Failed to delete notebook:", error);
        showDeleteModal.value = false;
        deletingNotebook.value = false;
    }
}

function openNote(id: string) {
    navigateTo(`/notes/${id}`);
}

// Filter management
function resetFilters() {
    noteFilter.keyword = "";
    noteFilter.tagIdList = [];
    noteFilter.star = false;
    noteFilter.sort = "desc";
}

function toggleStarFilter(value: boolean) {
    noteFilter.star = value;
}

function changeTag(value: string[] | null) {
    noteFilter.tagIdList = value ? value : [];
}

function changeSort(value: "asc" | "desc") {
    noteFilter.sort = value;
}

function searchKeyword(value: string) {
    noteFilter.keyword = value || "";
}

function loadMore() {
    if (!hasMore.value) return;
    fetchNotes(false);
}

// Lifecycle
onMounted(() => {
    loadNotebook();
    fetchNotes(true);
    fetchTags();

    if (loadMoreTrigger.value) {
        observer = new IntersectionObserver(
            (entries) => {
                const [entry] = entries;
                if (
                    entry?.isIntersecting &&
                    hasMore.value &&
                    !loadingMore.value
                ) {
                    loadMore();
                }
            },
            {
                root: scrollContainer.value,
                threshold: 1,
            },
        );
        observer.observe(loadMoreTrigger.value);
    }
});

onBeforeUnmount(() => {
    if (observer) observer.disconnect();
});

watch(
    () => ({ ...noteFilter }),
    () => {
        fetchNotes(true);
    },
    { deep: true },
);
</script>

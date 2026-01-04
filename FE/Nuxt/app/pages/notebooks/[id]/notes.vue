<template>
    <DashboardShell :scrollable="false">
        <template #sidebar>
            <Sidebar
                :notebooks="navItems"
                :co-notebooks="[]"
                :loading="navLoading"
                :current-id="route.params.id"
                :settings-sections="settingsSections"
                @load-more="fetchNav"
                @go-notebooks="goNotebooks"
                @go-co-notebooks="goCoNotebooks"
                @create="goCreate"
                @select="openNotebook"
                @select-co="openNotebook" />
        </template>

        <template #header>
            <div
                class="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between lg:gap-4">
                <div class="w-40">
                    <p class="text-sm text-slate-400">筆記</p>
                    <h1 class="text-2xl font-semibold">筆記列表</h1>
                </div>
                <div class="w-auto">
                    <NoteToolbar
                        :tags="tags"
                        :selected-tags="noteFilter.tagIdList"
                        :keyword="noteFilter.keyword"
                        :star-only="noteFilter.star"
                        :sort-order="noteFilter.sort"
                        :creating="creating"
                        @create="handleCreate"
                        @reset="resetFilters"
                        @toggle-star="toggleStarFilter"
                        @change-tag="changeTag"
                        @change-sort="changeSort"
                        @search="searchKeyword" />
                </div>
            </div>
        </template>

        <div class="flex flex-col h-full space-y-4">
            <UCard
                class="bg-slate-900/60 border-slate-800 flex-1 overflow-hidden">
                <div
                    ref="scrollContainer"
                    class="grid gap-4 md:grid-cols-2 xl:grid-cols-3 h-full overflow-y-auto pr-1 pb-2">
                    <template v-for="note in filteredNotes" :key="note.id">
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

                    <div
                        ref="loadMoreTrigger"
                        class="col-span-full flex items-center justify-center py-4 text-sm text-slate-400">
                        <UIcon
                            v-if="loadingMore"
                            name="i-lucide-loader-2"
                            class="w-5 h-5 animate-spin" />
                        <button
                            v-else-if="hasMore"
                            class="flex items-center gap-1 hover:text-accent"
                            @click="loadMore">
                            <UIcon
                                name="i-lucide-chevron-down"
                                class="w-4 h-4" />
                            載入更多
                        </button>

                        <span v-else>沒有更多筆記</span>
                    </div>
                </div>
            </UCard>
        </div>
    </DashboardShell>
</template>

<script setup lang="ts">
import {
    computed,
    onBeforeUnmount,
    onMounted,
    reactive,
    ref,
    watch,
    useRoute,
    useAuth,
} from "#imports";
import DashboardShell from "~/components/dashboard/DashboardShell.vue";
import Sidebar from "~/components/dashboard/Sidebar.vue";
import NoteToolbar from "~/components/note/NoteToolbar.vue";
import NoteCard from "~/components/ui/NoteCard.vue";
import { useNotebookNav } from "~/composables/useNotebookNav";
import { useNote } from "~/composables/model/useNote";
import { useTag } from "~/composables/model/useTag";
import type { Note } from "~~/types/Note";
import type { Tag } from "~~/types/Tag";

definePageMeta({ layout: "dashboard" });

const route = useRoute();
const auth = useAuth();
const dialogs = useDialogs();

const {
    items: navItems,
    loading: navLoading,
    fetchNotebooks: fetchNav,
} = useNotebookNav();
const { indexNotes, createNote, updateNote, deleteNote } = useNote();
const { indexTags } = useTag();

const settingsSections = [
    { label: "個人資料", value: "profile" },
    { label: "帳號安全", value: "security" },
    { label: "通知", value: "notification" },
];

const notes = ref<Note[]>([]);
const tags = ref<Tag[]>([]);
const noteFilter = reactive({
    keyword: "",
    tagIdList: [] as string[],
    star: false,
    sort: "desc" as "asc" | "desc",
});
const creating = ref(false);
const loading = ref(false);
const loadingMore = ref(false);
const page = ref(1);
const pageSize = 12;
const totalCount = ref(0);
const hasMore = computed(() => notes.value.length < totalCount.value);

const loadMoreTrigger = ref<HTMLElement | null>(null);
const scrollContainer = ref<HTMLElement | null>(null);
let observer: IntersectionObserver | null = null;

const userId = computed(() => auth.data.value?.user?.id || "");
const notebookId = computed(() => route.params.id as string);

const filteredNotes = computed(() => notes.value);

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

async function fetchTags() {
    const res = await indexTags();
    tags.value = (res.items as Tag[]) || [];
}

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
    } finally {
        creating.value = false;
    }
}

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

async function toggleNoteStar(id: string, next: boolean) {
    const target = notes.value.find((n) => n.id === id);
    if (!target) return;
    target.star = next;
    await updateNote({ ...target, notebookId: notebookId.value });
}

function deleteNoteItem(id: string) {
    dialogs.confirm("確定刪除？", "通知", async () => {
        await deleteNote(id);
        notes.value = notes.value.filter((n) => n.id !== id);
        totalCount.value = Math.max(0, totalCount.value - 1);
    });
}

function openNote(id: string) {
    navigateTo(`/notes/${id}`);
}

function goCreate() {
    navigateTo("/notebooks/create");
}

function goNotebooks() {
    navigateTo("/notebooks");
}

function goCoNotebooks() {
    navigateTo({ path: "/notebooks", query: { collab: "true" } });
}

function openNotebook(id: string) {
    navigateTo(`/notebooks/${id}/notes`);
}

onMounted(() => {
    fetchNotes(true);
    fetchTags();
    fetchNav();
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
                root: scrollContainer.value || undefined,
                threshold: 1,
            }
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
    { deep: true }
);

function loadMore() {
    if (!hasMore.value) return;
    fetchNotes(false);
}
</script>

<style scoped></style>

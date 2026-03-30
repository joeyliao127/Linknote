<template>
    <div class="notebooks-page">
        <div class="nb-header">
            <div class="nb-header-title">
                <p class="nb-subtitle">筆記本</p>
                <h1 class="nb-title">所有筆記本</h1>
            </div>
            <div class="nb-header-controls">
                <UInput
                    v-model="keyword"
                    icon="i-lucide-search"
                    placeholder="搜尋筆記本"
                    class="nb-search" />
                <UButton
                    icon="i-lucide-plus"
                    color="accent"
                    @click="goCreate">
                    新增
                </UButton>
            </div>
        </div>

        <div class="nb-content">
            <div class="nb-grid">
                <template v-for="item in notebooks" :key="item.id">
                    <NotebookCard
                        :id="item.id"
                        :title="item.title"
                        :description="item.description"
                        :note-count="item.noteCount"
                        :updated-at="item.updatedAt"
                        :collab="item.collab"
                        :deletable="true"
                        @open="openNotebook(item.id)"
                        @delete="deleteNotebookItem(item.id)"
                        @update-description="updateNotebookDescription" />
                </template>

                <div
                    ref="loadMoreTrigger"
                    class="nb-load-more col-span-full">
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
                    <span v-else>沒有更多筆記本</span>
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
    ref,
    useRouter,
    useAuth,
} from "#imports";
import NotebookCard from "~/components/ui/NotebookCard.vue";
import { useNotebook } from "~/composables/model/useNotebook";
import type { Notebook } from "~~/types/Notebook";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const auth = useAuth();
const { indexNotebook, deleteNotebook, updateNotebook } = useNotebook();

const notebooks = ref<Notebook[]>([]);
const page = ref(1);
const pageSize = 20;
const totalCount = ref(0);
const loading = ref(false);
const loadingMore = ref(false);
const keyword = ref("");
const hasMore = computed(() => notebooks.value.length < totalCount.value);

const loadMoreTrigger = ref<HTMLElement | null>(null);
let observer: IntersectionObserver | null = null;

const userId = computed(() => auth.data.value?.user?.id || "");

const mapNotebook = (
    item: Notebook & { noteCount?: number; collab?: boolean },
) => ({
    ...item,
    noteCount: item.noteCount ?? 0,
    collab: item.collab ?? false,
});

async function fetchList(reset = false) {
    if (loading.value || loadingMore.value) return;
    if (reset) {
        page.value = 1;
        notebooks.value = [];
    }
    const isLoadMore = !reset && notebooks.value.length > 0;
    if (isLoadMore) loadingMore.value = true;
    else loading.value = true;

    try {
        const res = await indexNotebook({
            page: page.value,
            pageSize,
            title: keyword.value || undefined,
        });
        const mapped = (res.items || []).map(mapNotebook);
        notebooks.value = reset ? mapped : [...notebooks.value, ...mapped];
        totalCount.value = res.count ?? notebooks.value.length;
        if (hasMore.value) page.value += 1;
    } finally {
        loading.value = false;
        loadingMore.value = false;
    }
}

function handleSearch() {
    fetchList(true);
}

function loadMore() {
    if (!hasMore.value) return;
    fetchList(false);
}

async function deleteNotebookItem(id: string) {
    await deleteNotebook(userId.value, id);
    notebooks.value = notebooks.value.filter((n) => n.id !== id);
    totalCount.value = Math.max(0, totalCount.value - 1);
}

async function updateNotebookDescription(id: string, description: string) {
    try {
        const notebook = notebooks.value.find((n) => n.id === id);
        if (!notebook) return;

        await updateNotebook(userId.value, id, {
            ...notebook,
            description,
        });

        notebook.description = description;
    } catch (error) {
        console.error("Failed to update notebook description:", error);
    }
}

function openNotebook(id: string) {
    navigateTo(`/notebooks/${id}/notes`);
}

function goCreate() {
    router.push("/notebooks/create");
}

onMounted(() => {
    fetchList(true);
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
                threshold: 1,
            },
        );
        observer.observe(loadMoreTrigger.value);
    }
});

onBeforeUnmount(() => {
    if (observer) observer.disconnect();
});
</script>

<style scoped>
.notebooks-page {
    display: flex;
    flex-direction: column;
    height: 100%;
    gap: 1.5rem;
    padding: 1.5rem;
}

.nb-header {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
}

@media (min-width: 768px) {
    .nb-header {
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }
}

.nb-header-title {
    flex-shrink: 0;
}

.nb-subtitle {
    font-size: 0.875rem;
    color: rgba(226, 232, 240, 0.5);
    margin: 0;
}

.nb-title {
    font-size: 1.875rem;
    font-weight: 600;
    color: rgba(248, 250, 252, 0.95);
    margin: 0;
    line-height: 1.2;
}

.nb-header-controls {
    display: flex;
    gap: 0.75rem;
    align-items: center;
}

.nb-search {
    min-width: 240px;
}

.nb-content {
    flex: 1;
    overflow-y: auto;
    padding-right: 0.5rem;
}

.nb-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
}

@media (max-width: 640px) {
    .nb-grid {
        grid-template-columns: 1fr;
    }
}

@media (min-width: 768px) and (max-width: 1024px) {
    .nb-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (min-width: 1024px) {
    .nb-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}

.nb-load-more {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    font-size: 0.875rem;
    color: rgba(226, 232, 240, 0.65);
}
</style>

<template>
    <div class="flex flex-col h-full gap-6 p-6">
        <!-- Header -->
        <div
            class="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
            <div class="shrink-0">
                <p class="text-sm text-slate-200/50">{{ $t('pages.notebooks.breadcrumb') }}</p>
                <h1
                    class="text-3xl font-semibold text-slate-50/95 leading-tight">
                    {{ $t('pages.notebooks.title') }}
                </h1>
            </div>
            <div class="flex gap-3 items-center">
                <UInput
                    v-model="keyword"
                    icon="i-lucide-search"
                    :placeholder="$t('pages.notebooks.searchPlaceholder')"
                    class="min-w-[240px]"
                    @keyup.enter="handleSearch" />
                <UButton
                    icon="i-lucide-plus"
                    color="primary"
                    class="text-white w-full"
                    @click="goCreate">
                    {{ $t('pages.notebooks.addNotebook') }}
                </UButton>
            </div>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto pr-2">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
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
                    class="col-span-full flex items-center justify-center p-4 text-sm text-slate-200/65">
                    <UIcon
                        v-if="loadingMore"
                        name="i-lucide-loader-2"
                        class="w-5 h-5 animate-spin" />
                    <button
                        v-else-if="hasMore"
                        class="flex items-center gap-1 hover:text-accent"
                        @click="loadMore">
                        <UIcon name="i-lucide-chevron-down" class="w-4 h-4" />
                        {{ $t('pages.notebooks.loadMore') }}
                    </button>
                    <span v-else>{{ $t('pages.notebooks.noMore') }}</span>
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
import { useNotebookNav } from "~/composables/useNotebookNav";
import type { Notebook } from "~~/types/Notebook";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const auth = useAuth();
const { indexNotebook, deleteNotebook, updateNotebook } = useNotebook();
const { fetchNotebooks } = useNotebookNav();

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
    fetchNotebooks(true);
}

async function updateNotebookDescription(id: string, description: string) {
    try {
        const notebook = notebooks.value.find((n) => n.id === id);
        if (!notebook) return;
        await updateNotebook(userId.value, id, { ...notebook, description });
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
            { threshold: 1 },
        );
        observer.observe(loadMoreTrigger.value);
    }
});

onBeforeUnmount(() => {
    if (observer) observer.disconnect();
});
</script>

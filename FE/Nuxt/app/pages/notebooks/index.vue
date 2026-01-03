<template>
    <DashboardShell>
        <template #sidebar>
            <Sidebar
                :notebooks="navItems"
                :co-notebooks="[]"
                :loading="navLoading"
                :current-id="navItems[0]?.id"
                :settings-sections="settingsSections"
                @load-more="fetchNav"
                @create="goCreate"
                @go-notebooks="goNotebooks"
                @go-co-notebooks="goCoNotebooks"
                @select="openNotebook"
                @select-co="openNotebook" />
        </template>

        <template #header>
            <div
                class="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between lg:gap-4">
                <div class="shrink-0">
                    <p class="text-sm text-slate-400">筆記本</p>
                    <h1 class="text-2xl font-semibold">所有筆記本</h1>
                </div>
                <div
                    class="flex justify-end items-center gap-2 flex-0 min-w-[280px] lg:min-w-[420px]">
                    <UInput
                        v-model="keyword"
                        icon="i-lucide-search"
                        placeholder="搜尋筆記本"
                        class="w-60"
                        @keyup.enter="handleSearch" />
                    <UButton
                        class="w-24"
                        icon="i-lucide-plus"
                        color="accent"
                        @click="goCreate">
                        新增
                    </UButton>
                </div>
            </div>
        </template>

        <div class="space-y-4">
            <UCard class="bg-slate-900/60 border-slate-800">
                <template #header>
                    <div class="flex items-center justify-between">
                        <p class="font-semibold">筆記本列表</p>
                        <p class="text-sm text-slate-400">
                            共 {{ totalCount }} 本
                        </p>
                    </div>
                </template>

                <div
                    ref="scrollContainer"
                    class="grid gap-4 md:grid-cols-2 xl:grid-cols-3 max-h-[640px] overflow-y-auto pr-1 pb-2">
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
                            @delete="deleteNotebookItem(item.id)" />
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
                        <span v-else>沒有更多筆記本</span>
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
    ref,
    useRouter,
    useAuth,
} from "#imports";
import DashboardShell from "~/components/dashboard/DashboardShell.vue";
import Sidebar from "~/components/dashboard/Sidebar.vue";
import NotebookCard from "~/components/ui/NotebookCard.vue";
import { useNotebook } from "~/composables/model/useNotebook";
import { useNotebookNav } from "~/composables/useNotebookNav";
import type { Notebook } from "~~/types/Notebook";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const auth = useAuth();
const { indexNotebook, deleteNotebook } = useNotebook();
const {
    items: navItems,
    loading: navLoading,
    fetchNotebooks: fetchNav,
} = useNotebookNav();

const settingsSections = [
    { label: "個人資料", value: "profile" },
    { label: "帳號安全", value: "security" },
    { label: "通知", value: "notification" },
];

const notebooks = ref<Notebook[]>([]);
const page = ref(1);
const pageSize = 12;
const totalCount = ref(0);
const loading = ref(false);
const loadingMore = ref(false);
const keyword = ref("");
const hasMore = computed(() => notebooks.value.length < totalCount.value);

const loadMoreTrigger = ref<HTMLElement | null>(null);
const scrollContainer = ref<HTMLElement | null>(null);
let observer: IntersectionObserver | null = null;

const userId = computed(() => auth.data.value?.user?.id || "");

const mapNotebook = (
    item: Notebook & { noteCount?: number; collab?: boolean }
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

function openNotebook(id: string) {
    navigateTo(`/notebooks/${id}/notes`);
}

function goCreate() {
    router.push("/notebooks/create");
}

function goNotebooks() {
    router.push("/notebooks");
}

function goCoNotebooks() {
    router.push({ path: "/notebooks", query: { collab: "true" } });
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
                root: scrollContainer.value || undefined,
                threshold: 1,
            }
        );
        observer.observe(loadMoreTrigger.value);
    }
    fetchNav();
});

onBeforeUnmount(() => {
    if (observer) observer.disconnect();
});
</script>

<style scoped></style>

<template>
    <div class="p-8 space-y-6">
        <h1 class="text-3xl font-bold text-primary">Sample Color Test</h1>
        <UBlogPost title="Introducing Nuxt Icon v1" />

        <div class="space-x-4 flex flex-col gap-4">
            <h2 class="title text-2xl">Buttons</h2>
            <div class="flex gap-2">
                <UButton
                    class="bg-primary px-4 py-2 rounded hover:bg-primary/90">
                    Primary
                </UButton>
                <UButton class="bg-accent px-4 py-2 rounded hover:bg-accent/90">
                    Accent
                </UButton>

                <UFieldGroup>
                    <UTooltip text="TODO">
                        <UButton variant="outline" icon="uil-mail" />
                    </UTooltip>
                    <UButton variant="outline" icon="uil-github" />
                </UFieldGroup>
            </div>
            <h3 class="title text-xl">Button with icon</h3>
            <div class="flex gap-2"></div>
            <h3 class="title text-xl">Variants</h3>
            <div class="flex gap-2">
                <UButton variant="solid" label="sloid" />
                <UButton variant="outline" label="outline" />
                <UButton variant="subtle" label="subtle" />
                <UButton variant="soft" label="soft" />
                <UButton variant="ghost" label="ghost" />
                <UButton variant="link" label="link" />
                <UButton variant="solid" label="sloid" color="secondary" />
                <UButton variant="outline" label="outline" color="accent" />
                <UButton variant="subtle" label="subtle" color="accent" />
                <UButton variant="soft" label="soft" color="accent" />
                <UButton variant="ghost" label="ghost" color="accent" />
                <UButton variant="link" label="link" color="accent" />
            </div>
        </div>
        <UCarousel></UCarousel>

        <USeparator>
            <h2 class="title text-2xl">Card</h2>
        </USeparator>

        <div class="w-100 p-2">
            <CardContainer>
                <template #card>
                    <NoteCard
                        v-for="value in noteItems"
                        class="w-100"
                        :id="value.id"
                        :title="`筆記${value.title}`"
                        :tags="value.tags"
                        :starred="value.starred" />
                </template>
            </CardContainer>
        </div>

        <USeparator>
            <h2 class="title text-2xl">筆記 Star Item</h2>
        </USeparator>

        <div class="w-100 p-2">
            <template v-for="value in noteItems">
                <StarItem
                    :id="value.id"
                    :title="`筆記${value.title}`"
                    :active="value.active"
                    :stared="value.stared" />
            </template>
        </div>

        <USeparator>
            <h2 class="title text-2xl">Charts and Cards</h2>
        </USeparator>
        <div class="grid grid-cols-4 gap-6">
            <UCard class="h-50"></UCard>
            <UCard class="h-50"></UCard>
            <UCard class="h-50"></UCard>
            <UCard class="h-50">
                <Bar :data="chartData" :options="chartOptions" />
            </UCard>

            <UCard class="h-50 col-span-4">
                <Bar :data="chartData" :options="chartOptions" />
            </UCard>
        </div>

        <USeparator>
            <h2>Avatar</h2>
        </USeparator>
        <div class="px-4">
            <UAvatar src="https://github.com/benjamincanac.png" />
            <UAvatarGroup>
                <UAvatar src="https://github.com/benjamincanac.png" />
                <UAvatar src="https://github.com/benjamincanac.png" />
                <UAvatar src="https://github.com/benjamincanac.png" />
            </UAvatarGroup>
        </div>

        <USeparator>
            <h2 class="title text-2xl">Sigin Form</h2>
        </USeparator>

        <FormSignTabs />

        <USeparator>
            <h2 class="title text-2xl">Dashboard + Components Demo</h2>
        </USeparator>

        <div class="rounded-2xl border border-slate-800 overflow-hidden">
            <DashboardShell :full-height="false">
                <template #sidebar>
                    <Sidebar
                        :notebooks="demoNotebooks"
                        :has-more="sidebarHasMore"
                        :loading="sidebarLoading"
                        :current-id="activeNotebookId"
                        @select="handleSelectNotebook"
                        @create="handleCreateNotebook"
                        @load-more="handleLoadMoreNotebooks" />
                </template>

                <template #header>
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-lg font-semibold">
                                DashboardShell 範例
                            </p>
                            <p class="text-sm text-slate-400">
                                sidebar + toolbar + cards 假資料展示
                            </p>
                        </div>
                        <UButton variant="ghost" icon="i-lucide-bell" />
                    </div>
                </template>

                <div class="space-y-6">
                    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                        <NotebookCard
                            v-for="notebook in demoNotebooks"
                            :key="notebook.id"
                            v-bind="notebook"
                            :deletable="!notebook.collab"
                            @open="handleOpenNotebook"
                            @delete="handleDeleteNotebook" />
                    </div>

                    <NoteToolbar
                        :tags="demoTags"
                        :selected-tag="selectedTagId"
                        :keyword="keyword"
                        :star-only="starOnly"
                        :sort-order="sortOrder"
                        :creating="creatingNote"
                        @create="handleCreateNote"
                        @reset="handleResetFilters"
                        @toggle-star="handleToggleStarFilter"
                        @change-tag="handleChangeTag"
                        @change-sort="handleChangeSort"
                        @search="handleSearchKeyword" />

                    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                        <NoteCard
                            v-for="note in filteredNotes"
                            :key="note.id"
                            :id="note.id"
                            :title="note.title"
                            :description="note.keypoint"
                            :tags="note.tags"
                            :starred="note.starred"
                            :updated-at="note.updatedAt"
                            @toggle-star="handleToggleNoteStar"
                            @delete="handleDeleteNote"
                            @open="handleOpenNote" />
                    </div>

                    <div class="max-w-xl">
                        <UCard>
                            <template #header>
                                <p class="font-semibold">NotebookForm 範例</p>
                            </template>
                            <NotebookForm
                                :submitting="submittingNotebook"
                                submit-label="建立"
                                @submit="handleNotebookFormSubmit" />
                        </UCard>
                    </div>
                </div>
            </DashboardShell>
        </div>
    </div>
</template>
<script setup lang="ts">
import { Bar } from "vue-chartjs";
import { computed, reactive, ref } from "vue";
import { useToast } from "#imports";
import StarItem from "~/components/ui/StarItem.vue";
import CardContainer from "~/components/ui/CardContainer.vue";
import NoteCard from "~/components/ui/NoteCard.vue";
import DashboardShell from "~/components/dashboard/DashboardShell.vue";
import Sidebar from "~/components/dashboard/Sidebar.vue";
import NotebookCard from "~/components/ui/NotebookCard.vue";
import NoteToolbar from "~/components/note/NoteToolbar.vue";
import NotebookForm from "~/components/notebook/NotebookForm.vue";
import type { Tag } from "~~/types/Tag";

const toast = useToast();

const noteItems = ref([
    {
        id: "1",
        title: "111111111111111111111111111111111111111111111",
        active: true,
        tags: [
            {
                id: "1",
                title: "tag1",
                userId: "12345",
                createdAt: "2025-12-31",
                updatedAt: "2025-12-31",
            },
        ],
        starred: false,
        stared: false,
    },
    {
        id: "2",
        title: "2",
        active: false,
        tags: [
            {
                id: "2",
                title: "tag1",
                userId: "12345",
                createdAt: "2025-12-31",
                updatedAt: "2025-12-31",
            },
        ],
        starred: true,
        stared: true,
    },
    {
        id: "3",
        title: "3",
        active: false,
        tags: [
            {
                id: "3",
                title: "tag1",
                userId: "12345",
                createdAt: "2025-12-31",
                updatedAt: "2025-12-31",
            },
        ],
        starred: false,
        stared: false,
    },
    {
        id: "4",
        title: "4",
        active: false,
        tags: [
            {
                id: "4",
                title: "tag1",
                userId: "12345",
                createdAt: "2025-12-31",
                updatedAt: "2025-12-31",
            },
        ],
        starred: false,
        stared: false,
    },
    {
        id: "5",
        title: "5",
        active: false,
        tags: [
            {
                id: "5",
                title: "tag1",
                userId: "12345",
                createdAt: "2025-12-31",
                updatedAt: "2025-12-31",
            },
        ],
        starred: false,
        stared: false,
    },
] as const);

const chartData = ref({
    labels: [
        "圖表一",
        "圖表二",
        "圖表三",
        "圖表四",
        "圖表五",
        "圖表六",
        "圖表七",
    ],
    datasets: [
        {
            label: "測試資料",
            data: [65, 59, 80, 81, 56, 55, 40],
            backgroundColor: [
                "rgba(255, 99, 132, 0.2)",
                "rgba(255, 159, 64, 0.2)",
                "rgba(255, 205, 86, 0.2)",
                "rgba(75, 192, 192, 0.2)",
                "rgba(54, 162, 235, 0.2)",
                "rgba(153, 102, 255, 0.2)",
                "rgba(201, 203, 207, 0.2)",
            ],
            borderColor: [
                "rgb(255, 99, 132)",
                "rgb(255, 159, 64)",
                "rgb(255, 205, 86)",
                "rgb(75, 192, 192)",
                "rgb(54, 162, 235)",
                "rgb(153, 102, 255)",
                "rgb(201, 203, 207)",
            ],
            borderWidth: 1,
        },
    ],
});

const chartOptions = ref({
    //表示圖表會 自動根據容器大小調整尺寸
    responsive: true,
    // 是否要維持原生的 長寬比例（aspect ratio）
    maintainAspectRatio: false,
    scales: {
        // 控制 y 軸的起始值是否從 0 開始
        y: { beginAtZero: true },
    },
    plugins: {
        //隱藏圖表右上角的「圖例（legend）」
        legend: { display: false },
    },
});

const demoTags = ref<Tag[]>([
    {
        id: "tag-frontend",
        title: "前端",
        userId: "u-1",
        createdAt: new Date(),
        updatedAt: new Date(),
    },
    {
        id: "tag-design",
        title: "設計",
        userId: "u-1",
        createdAt: new Date(),
        updatedAt: new Date(),
    },
    {
        id: "tag-rbac",
        title: "RBAC",
        userId: "u-1",
        createdAt: new Date(),
        updatedAt: new Date(),
    },
]);

const demoNotebooks = ref([
    {
        id: "nb-1",
        title: "Linknote UI",
        description: "Nuxt UI 元件與 dashboard 規劃",
        noteCount: 12,
        updatedAt: new Date(),
        owner: "Joey",
        collab: false,
    },
    {
        id: "nb-2",
        title: "共編範例",
        description: "與團隊協作的筆記本",
        noteCount: 8,
        updatedAt: new Date(),
        owner: "Team",
        collab: true,
    },
    {
        id: "nb-3",
        title: "架構決策",
        description: "ADR、BFF、RBAC 紀錄",
        noteCount: 5,
        updatedAt: new Date(),
        owner: "Joey",
        collab: false,
    },
]);

const activeNotebookId = ref(demoNotebooks.value[0]?.id ?? "");
const sidebarLoading = ref(false);
const sidebarHasMore = ref(true);

const demoNotes = ref(
    [
        {
            id: "note-1",
            title: "Nuxt UI Dashboard",
            keypoint: "Dashboard layout + sidebar",
            starred: true,
            tags: [demoTags.value[0], demoTags.value[1]],
            updatedAt: new Date(),
        },
        {
            id: "note-2",
            title: "RBAC 權限",
            keypoint: "後端 ACL 與前端 BFF",
            starred: false,
            tags: [demoTags.value[2]],
            updatedAt: new Date(Date.now() - 1000 * 60 * 60 * 24),
        },
        {
            id: "note-3",
            title: "UI 調色盤",
            keypoint: "按鈕、卡片、Badge 配色",
            starred: false,
            tags: [demoTags.value[1]],
            updatedAt: new Date(Date.now() - 1000 * 60 * 60 * 6),
        },
    ].map((item) => reactive(item))
);

const starOnly = ref(false);
const selectedTagId = ref<string | null>(null);
const keyword = ref("");
const sortOrder = ref<"asc" | "desc">("desc");
const creatingNote = ref(false);
const submittingNotebook = ref(false);

const filteredNotes = computed(() => {
    let list = [...demoNotes.value];
    if (starOnly.value) {
        list = list.filter((note) => note.starred);
    }
    if (selectedTagId.value) {
        list = list.filter((note) =>
            note.tags.some((tag) => tag.id === selectedTagId.value)
        );
    }
    if (keyword.value) {
        const lower = keyword.value.toLowerCase();
        list = list.filter((note) =>
            (note.title || "").toLowerCase().startsWith(lower)
        );
    }
    list.sort((a, b) => {
        const aTime = new Date(a.updatedAt).getTime();
        const bTime = new Date(b.updatedAt).getTime();
        return sortOrder.value === "asc" ? aTime - bTime : bTime - aTime;
    });
    return list;
});

function handleSelectNotebook(id: string) {
    activeNotebookId.value = id;
    toast.add({
        title: "切換筆記本",
        description: `active: ${id}`,
    });
}

function handleCreateNotebook() {
    toast.add({
        title: "新增筆記本",
        description: "觸發 create 事件",
        color: "accent",
    });
}

function handleLoadMoreNotebooks() {
    if (sidebarLoading.value || !sidebarHasMore.value) return;
    sidebarLoading.value = true;
    setTimeout(() => {
        demoNotebooks.value.push({
            id: `nb-${Date.now()}`,
            title: "更多筆記本",
            description: "Infinite scroll 模擬資料",
            noteCount: 3,
            updatedAt: new Date(),
            owner: "Demo",
            collab: false,
        });
        sidebarLoading.value = false;
        sidebarHasMore.value = false;
    }, 600);
}

function handleOpenNotebook(id: string) {
    toast.add({
        title: "開啟 Notebook",
        description: id,
    });
}

function handleDeleteNotebook(id: string) {
    demoNotebooks.value = demoNotebooks.value.filter(
        (notebook) => notebook.id !== id
    );
    toast.add({
        title: "刪除 Notebook",
        description: id,
        color: "red",
    });
}

function handleCreateNote() {
    creatingNote.value = true;
    setTimeout(() => {
        demoNotes.value.unshift(
            reactive({
                id: `note-${Date.now()}`,
                title: "新筆記",
                keypoint: "預設 keypoint",
                starred: false,
                tags: [demoTags.value[0]],
                updatedAt: new Date(),
            })
        );
        creatingNote.value = false;
    }, 500);
}

function handleToggleNoteStar(id: string, next: boolean) {
    const target = demoNotes.value.find((note) => note.id === id);
    if (target) target.starred = next;
}

function handleDeleteNote(id: string) {
    demoNotes.value = demoNotes.value.filter((note) => note.id !== id);
}

function handleOpenNote(id: string) {
    toast.add({
        title: "開啟 Note",
        description: id,
    });
}

function handleResetFilters() {
    starOnly.value = false;
    selectedTagId.value = null;
    keyword.value = "";
    sortOrder.value = "desc";
}

function handleToggleStarFilter(value: boolean) {
    starOnly.value = value;
}

function handleChangeTag(value: string | null) {
    selectedTagId.value = value;
}

function handleChangeSort(value: "asc" | "desc") {
    sortOrder.value = value;
}

function handleSearchKeyword(value: string) {
    keyword.value = value || "";
}

function handleNotebookFormSubmit(value: {
    title: string;
    description?: string;
    active?: boolean;
}) {
    submittingNotebook.value = true;
    setTimeout(() => {
        demoNotebooks.value.unshift({
            id: `nb-${Date.now()}`,
            title: value.title,
            description: value.description ?? "",
            noteCount: 0,
            updatedAt: new Date(),
            owner: "你",
            collab: false,
        });
        submittingNotebook.value = false;
        toast.add({
            title: "Notebook 已建立（假資料）",
            color: "accent",
        });
    }, 400);
}
</script>

<style>
@reference '~/assets/css/tailwind.css';

.title {
    @apply text-white pb-4 font-medium;
}
</style>

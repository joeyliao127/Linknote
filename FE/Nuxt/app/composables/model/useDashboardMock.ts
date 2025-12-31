import { computed } from "vue";
import type { Tag } from "~~/types/Tag";

type OverviewStat = {
    label: string;
    value: number;
    icon: string;
};

type RecentNote = {
    id: string;
    title: string;
    notebookId: string;
    updatedAt: string | Date;
    tags?: Tag[];
};

type NotebookItem = {
    id: string;
    title: string;
    noteCount: number;
    updatedAt: string | Date;
    collab?: boolean;
};

export const useDashboardMock = () => {
    const overview = computed<OverviewStat[]>(() => [
        { label: "筆記本", value: 12, icon: "i-lucide-notebook-pen" },
        { label: "筆記", value: 128, icon: "i-lucide-sticky-note" },
        { label: "共編", value: 4, icon: "i-lucide-users" },
    ]);

    const recentNotes = computed<RecentNote[]>(() => [
        {
            id: "note-1",
            title: "Nuxt UI Dashboard 初版",
            notebookId: "nb-1",
            updatedAt: new Date(),
            tags: [{ id: "t1", title: "UI", userId: "", createdAt: "", updatedAt: "" }],
        },
        {
            id: "note-2",
            title: "RBAC 權限整理",
            notebookId: "nb-2",
            updatedAt: new Date(Date.now() - 1000 * 60 * 60 * 24),
        },
        {
            id: "note-3",
            title: "筆記搜尋需求",
            notebookId: "nb-3",
            updatedAt: new Date(Date.now() - 1000 * 60 * 60 * 6),
        },
    ]);

    const notebooks = computed<NotebookItem[]>(() => [
        {
            id: "nb-1",
            title: "Linknote UI",
            noteCount: 12,
            updatedAt: new Date(),
        },
        {
            id: "nb-2",
            title: "共編範例",
            noteCount: 8,
            updatedAt: new Date(Date.now() - 1000 * 60 * 60 * 12),
            collab: true,
        },
        {
            id: "nb-3",
            title: "架構決策",
            noteCount: 5,
            updatedAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2),
        },
    ]);

    return {
        overview,
        recentNotes,
        notebooks,
    };
};

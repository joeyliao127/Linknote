import { useToast, useState } from "#imports";
import { useNotebook } from "~/composables/model/useNotebook";
import type { Notebook } from "~~/types/Notebook";

export type NotebookNavItem = {
    id: string;
    title: string;
    updatedAt?: string | Date;
    noteCount?: number;
    collab?: boolean;
};

export const useNotebookNav = () => {
    const { indexNotebook } = useNotebook();
    const toast = useToast();

    const items = useState<NotebookNavItem[]>("notebook-nav-items", () => []);
    const loading = useState<boolean>("notebook-nav-loading", () => false);
    const error = useState<string | null>("notebook-nav-error", () => null);
    const initialized = useState<boolean>("notebook-nav-initialized", () => false);

    const coItems = useState<NotebookNavItem[]>("notebook-nav-co-items", () => []);
    const coLoading = useState<boolean>("notebook-nav-co-loading", () => false);
    const coInitialized = useState<boolean>("notebook-nav-co-initialized", () => false);

    const mapNotebook = (notebook: Notebook): NotebookNavItem => ({
        id: notebook.id,
        title: notebook.title,
        updatedAt: notebook.updatedAt,
        noteCount: (notebook as any).noteCount ?? 0,
        collab: (notebook as any).collab ?? false,
    });

    const fetchNotebooks = async (force = false) => {
        if (loading.value) return;
        if (initialized.value && !force) return;
        loading.value = true;
        error.value = null;

        try {
            const res = await indexNotebook();
            items.value = (res.items || []).map(mapNotebook);
            initialized.value = true;
        } catch (err: any) {
            error.value = err?.data?.message || err?.message || "取得筆記本失敗";
            toast.add({
                title: "載入失敗",
                description: error.value,
                color: "red",
            });
        } finally {
            loading.value = false;
        }
    };

    const fetchCoNotebooks = async (force = false) => {
        if (coLoading.value) return;
        if (coInitialized.value && !force) return;
        coLoading.value = true;

        try {
            const res = await indexNotebook({ collab: true, pageSize: 50 });
            coItems.value = (res.items || []).map(mapNotebook);
            coInitialized.value = true;
        } catch {
            // silent
        } finally {
            coLoading.value = false;
        }
    };

    return {
        items,
        loading,
        error,
        fetchNotebooks,
        coItems,
        coLoading,
        fetchCoNotebooks,
    };
};

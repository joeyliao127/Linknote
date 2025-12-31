import { ref } from "vue";
import { useToast } from "#imports";
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

    const items = ref<NotebookNavItem[]>([]);
    const loading = ref(false);
    const error = ref<string | null>(null);

    const mapNotebook = (notebook: Notebook): NotebookNavItem => ({
        id: notebook.id,
        title: notebook.title,
        updatedAt: notebook.updatedAt,
        noteCount: (notebook as any).noteCount ?? 0,
        collab: (notebook as any).collab ?? false,
    });

    const fetchNotebooks = async () => {
        if (loading.value) return;
        loading.value = true;
        error.value = null;

        try {
            const res = await indexNotebook();
            items.value = (res.items || []).map(mapNotebook);
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

    return {
        items,
        loading,
        error,
        fetchNotebooks,
    };
};

import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~/types";
import type {
    Notebook,
    CreateNotebookDTO,
    UpdateNotebookDTO,
} from "~/types/Notebook";

const _useNotebook = () => {
    const runtimeConfig = useRuntimeConfig();

    const indexNotebook = async (
        userId: string
    ): Promise<Pagination<Notebook>> => {
        const response: Pagination<Notebook> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks`,
            {
                method: "GET",
                headers: {
                    Authorization: userId,
                },
            }
        );

        return response;
    };

    const createNotebook = async (
        userId: string,
        notebook: CreateNotebookDTO
    ) => {
        const response: Pagination<Notebook> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks`,
            {
                method: "POST",
                headers: {
                    Authorization: userId,
                },
                body: notebook,
            }
        );

        return response;
    };

    const updateNotebook = async (
        userId: string,
        notebookId: string,
        notebook: UpdateNotebookDTO
    ) => {
        const response: Pagination<Notebook> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks/${notebookId}`,
            {
                method: "PUT",
                headers: {
                    Authorization: userId,
                },
                body: notebook,
            }
        );

        return response;
    };

    const deleteNotebook = async (userId: string, notebookId: string) => {
        const response: Pagination<Notebook> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks/${notebookId}`,
            {
                method: "DELETE",
                headers: {
                    Authorization: userId,
                },
            }
        );
    };

    return {
        indexNotebook,
        createNotebook,
        updateNotebook,
        deleteNotebook,
    };
};

export const useNotebook = _useNotebook;

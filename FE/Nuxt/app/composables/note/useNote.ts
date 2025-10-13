import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~/types";
import type { Note, CreateNoteDTO, UpdateNoteDTO } from "~/types/Note";

const _useNote = () => {
    const runtimeConfig = useRuntimeConfig();

    const indexNotes = async (
        notebookId: string,
        userId: string
    ): Promise<Pagination<Note>> => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks/${notebookId}/notes`,
            {
                method: "GET",
                headers: {
                    Authorization: userId,
                },
                query: {
                    notebookId,
                },
            }
        );

        console.log(response);

        return response;
    };

    const getNoteById = async () => {};

    const createNote = async () => {};

    const updateNote = async () => {};

    const deleteNote = async () => {};

    return {
        indexNotes,
        getNoteById,
        createNote,
        updateNote,
        deleteNote,
    };
};

export const useNote = _useNote;

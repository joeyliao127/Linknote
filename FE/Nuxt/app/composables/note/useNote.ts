import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~/types";
import type { Note, CreateNoteDTO, UpdateNoteDTO } from "~/types/Note";

const _useNote = () => {
    const runtimeConfig = useRuntimeConfig();

    const indexNotes = async (
        userId: string,
        notebookId: string
    ): Promise<Pagination<Note>> => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks/${notebookId}/notes`,
            {
                method: "GET",
                headers: {
                    Authorization: userId,
                },
            }
        );

        return response;
    };

    const createNote = async (userId: string, note: CreateNoteDTO) => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notes`,
            {
                method: "POST",
                headers: {
                    Authorization: userId,
                },
                body: note,
            }
        );

        return response;
    };

    const updateNote = async (userId: string, note: UpdateNoteDTO) => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notes/${note.id}`,
            {
                method: "PUT",
                headers: {
                    Authorization: userId,
                },
                body: note,
            }
        );

        return response;
    };

    const deleteNote = async (userId: string, noteId: string) => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notes/${noteId}`,
            {
                method: "DELETE",
                headers: {
                    Authorization: userId,
                },
            }
        );
    };

    return {
        indexNotes,
        createNote,
        updateNote,
        deleteNote,
    };
};

export const useNote = _useNote;

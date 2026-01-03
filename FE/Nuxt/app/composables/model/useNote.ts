import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~~/types";
import type { Note, CreateNoteDTO, UpdateNoteDTO } from "~~/types/Note";
import { toSelection } from "../utils/useFormat";

const _useNote = () => {
    const runtimeConfig = useRuntimeConfig();

    const indexNotes = async (
        userId: string,
        notebookId: string,
        query?: {
            page?: number;
            pageSize?: number;
            title?: string;
            tagIdList?: string[] | null;
            star?: boolean;
            sort?: "asc" | "desc";
        }
    ): Promise<Pagination<Note>> => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notebooks/${notebookId}/notes`,
            {
                method: "GET",
                headers: {
                    Authorization: userId,
                },
                query,
            }
        );
        const items: Note[] = [];
        for (let note of response.items) {
            note.tagIdList = note.tags.map((item) => item.id);
            items.push(note);
        }
        response.items = items;
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

    const addTags = async (noteId: string, tagIdList: string[]) => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notes/${noteId}/tags`,
            {
                method: "PUT",
                body: {
                    tagIdList,
                },
            }
        );
    };

    return {
        indexNotes,
        createNote,
        updateNote,
        deleteNote,
        addTags,
    };
};

export const useNote = _useNote;

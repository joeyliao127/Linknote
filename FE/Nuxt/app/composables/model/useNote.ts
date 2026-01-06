import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~~/types";
import type { Note, CreateNoteDTO, UpdateNoteDTO } from "~~/types/Note";
import { toSelection } from "../utils/useFormat";

const _useNote = () => {
    const runtimeConfig = useRuntimeConfig();

    const indexNotes = async (
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

    const getNote = async (noteId: string): Promise<Note> => {
        const response: Note = await $fetch(
            `${runtimeConfig.public.API_URL}/notes/${noteId}`,
            {
                method: "GET",
            }
        );
        // response.tagIdList = response.tags.map((item) => item.id);
        return response;
    };

    const createNote = async (note: CreateNoteDTO) => {
        const response: Note = await $fetch(
            `${runtimeConfig.public.API_URL}/notes`,
            {
                method: "POST",
                body: note,
            }
        );

        return response;
    };

    const updateNote = async (note: UpdateNoteDTO) => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notes/${note.id}`,
            {
                method: "PUT",
                body: note,
            }
        );

        return response;
    };

    const deleteNote = async (noteId: string) => {
        const response: Pagination<Note> = await $fetch(
            `${runtimeConfig.public.API_URL}/notes/${noteId}`,
            {
                method: "DELETE",
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
        getNote,
        createNote,
        updateNote,
        deleteNote,
        addTags,
    };
};

export const useNote = _useNote;

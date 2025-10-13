import type { Tag } from "./Tag";

export interface Note {
    id: string;
    notebookId: string;
    title: string;
    content: string;
    question: string;
    keypoint: string;
    star: boolean;
    createdAt: Date | string;
    updatedAt: Date | string;
    tags: Tag[];
}

export type CreateNoteDTO = Omit<
    Note,
    "id" | "createdAt" | "updatedAt" | "tags"
>;

export type UpdateNoteDTO = Omit<Note, "updatedAt" | "tags">;

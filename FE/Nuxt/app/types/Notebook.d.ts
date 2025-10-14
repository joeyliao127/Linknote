import type { Pagination } from ".";
import type { Note } from "./note";

export interface Notebook {
    id: string;
    title: string;
    description?: string;
    active?: boolean;
    createdAt: Date | string;
    updatedAt: Date | string;
}

export type CreateNotebookDTO = Omit<
    Notebook,
    "id" | "active" | "createdAt" | "updatedAt"
>;

export type UpdateNotebookDTO = Omit<
    Notebook,
    "id" | "createdAt" | "updatedAt"
>;

import type { Pagination } from ".";

// TODO: add fileds
export interface Notebook {
    id: string;
    title: string;
    description?: string;
    active?: boolean;
    createdAt: Date | string;
    updatedAt: Date | string;
}

// TODO: omit fileds
export type CreateNotebookDTO = Omit<
    Notebook,
    "id" | "active" | "createdAt" | "updatedAt"
>;

// TODO: omit fileds
export type UpdateNotebookDTO = Omit<
    Notebook,
    "id" | "createdAt" | "updatedAt"
>;

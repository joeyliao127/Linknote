export interface Tag {
    id: string;
    userId: string;
    title: string;
    createdAt: Date | string;
    UpdateddAt: Date | string;
}

export type CreateTagDTO = Omit<Tag, "id" | "createdAt" | "updatedAt">;

export type UpdateTagDTO = Omit<Tag, "createdAt" | "updatedAt">;

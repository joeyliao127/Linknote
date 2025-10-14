export interface Tag {
    id: string;
    userId: string;
    title: string;
    createdAt: Date | string;
    updatedAt: Date | string;
}

export type CreateTagDTO = Omit<
    Tag,
    "id" | "userId" | "createdAt" | "updatedAt"
>;

export type UpdateTagDTO = Omit<Tag, "userId" | "createdAt" | "updatedAt">;

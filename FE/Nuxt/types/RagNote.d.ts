export interface RagNote {
    noteId: string;
    userId: string;
    notebookId: string;
    noteUpdatedAt: string;
    ragUpdatedAt: string;
    createdAt: string;
    noteTitle: string | null;
    status: "up_to_date" | "outdated" | "deleted";
    behindDays: number;
}

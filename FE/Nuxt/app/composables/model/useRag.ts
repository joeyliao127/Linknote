export const useRag = () => {
    const ingest = (noteIds: string[]) =>
        $fetch("/api/rag/ingest", {
            method: "POST",
            body: { noteIds },
        });

    const getRagNotes = (notebookId: string) =>
        $fetch<import("~~/types/RagNote").RagNote[]>("/api/rag/notes", {
            query: { notebookId },
        });

    return { ingest, getRagNotes };
};

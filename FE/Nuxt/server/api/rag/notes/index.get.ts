export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const { notebookId } = getQuery(event) as { notebookId: string };
    const config = useRuntimeConfig();

    // 1. 取得已 ingest 的 rag_notes 清單
    const ragNotes = await $fetch<any[]>(`${config.RESOURCE_API}/rag-notes`, {
        headers: { Authorization: `Bearer ${session?.token}` },
        query: { notebookId },
    });

    if (!ragNotes.length) return [];

    // 2. 取得該 notebook 的所有 notes（取得 updated_at 用於版本比對）
    const notesPage = await $fetch<{ items: any[] }>(
        `${config.RESOURCE_API}/notebooks/${notebookId}/notes`,
        {
            headers: { Authorization: `Bearer ${session?.token}` },
            query: { pageSize: 100 },
        }
    );
    const noteMap = new Map((notesPage.items ?? []).map((n: any) => [n.id, n]));

    // 3. 合併並計算版本狀態
    return ragNotes.map((rn) => {
        const note = noteMap.get(rn.noteId);
        if (!note) {
            return { ...rn, noteTitle: null, status: "deleted", behindDays: 0 };
        }
        const diffMs = new Date(note.updatedAt).getTime() - new Date(rn.noteUpdatedAt).getTime();
        const isOutdated = diffMs > 0;
        return {
            ...rn,
            noteTitle: note.title,
            noteUpdatedAt: note.updatedAt,
            ragUpdatedAt: rn.noteUpdatedAt,
            status: isOutdated ? "outdated" : "up_to_date",
            behindDays: isOutdated ? Math.ceil(diffMs / (1000 * 60 * 60 * 24)) : 0,
        };
    });
});

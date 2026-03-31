export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const { notebookId } = getQuery(event) as { notebookId?: string };
    const config = useRuntimeConfig();
    const authHeader = { Authorization: `Bearer ${session?.token}` };

    // 1. 取得已 ingest 的 rag_notes（有 notebookId 就篩選，否則取全部）
    const ragNotes = await $fetch<any[]>(`${config.RESOURCE_API}/rag-notes`, {
        headers: authHeader,
        query: notebookId ? { notebookId } : {},
    });

    if (!ragNotes.length) return [];

    // 2. 分組 notebookId → 批次取得各 notebook 的 notes
    const notebookIds = [...new Set(ragNotes.map((rn) => rn.notebookId).filter(Boolean))];

    const noteMap = new Map<string, any>();
    await Promise.all(
        notebookIds.map(async (nbId) => {
            try {
                const res = await $fetch<{ items: any[] }>(
                    `${config.RESOURCE_API}/notebooks/${nbId}/notes`,
                    { headers: authHeader, query: { pageSize: 200 } },
                );
                for (const note of res.items ?? []) {
                    noteMap.set(note.id, note);
                }
            } catch {}
        }),
    );

    // 3. 合併並計算版本狀態
    return ragNotes.map((rn) => {
        const note = noteMap.get(rn.noteId);
        if (!note) {
            return { ...rn, noteTitle: null, status: "deleted", behindDays: 0 };
        }
        const diffMs =
            new Date(note.updatedAt).getTime() -
            new Date(rn.noteUpdatedAt).getTime();
        const isOutdated = diffMs > 0;
        return {
            ...rn,
            noteTitle: note.title,
            noteUpdatedAt: note.updatedAt,
            ragUpdatedAt: rn.noteUpdatedAt,
            status: isOutdated ? "outdated" : "up_to_date",
            behindDays: isOutdated
                ? Math.ceil(diffMs / (1000 * 60 * 60 * 24))
                : 0,
        };
    });
});

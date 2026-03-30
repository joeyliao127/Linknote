export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const { noteIds } = await readBody<{ noteIds: string[] }>(event);
    const config = useRuntimeConfig();

    // 1. Fetch full note content from Spring Boot (batch)
    const notes = await $fetch<any[]>(`${config.RESOURCE_API}/notes/batch`, {
        method: "POST",
        headers: { Authorization: `Bearer ${session?.token}` },
        body: { noteIds },
    });

    // 2. Send to FastAPI /ingest（pipeline: split → embed → store）
    await $fetch(`${config.RAG_API}/ingest`, {
        method: "POST",
        body: {
            notes: notes.map((note) => ({
                note_id: note.id,
                user_id: session?.user?.userId,
                notebook_id: note.notebookId,
                title: note.title ?? "",
                question: note.question ?? null,
                content: note.content ?? null,
                keypoint: note.keypoint ?? null,
            })),
        },
    });

    // 3. Upsert rag_notes records in Spring Boot（記錄 ingest 時間與 note 版本）
    await Promise.all(
        notes.map((note) =>
            $fetch(`${config.RESOURCE_API}/rag-notes`, {
                method: "POST",
                headers: { Authorization: `Bearer ${session?.token}` },
                body: {
                    noteId: note.id,
                    notebookId: note.notebookId,
                    noteUpdatedAt: note.updatedAt,
                },
            }),
        ),
    );

    return { success: true, count: notes.length };
});

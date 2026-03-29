export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const noteId = getRouterParam(event, "noteId") as string;
    const config = useRuntimeConfig();

    // 1. 刪除 Qdrant 中該 note 的所有 chunks
    await $fetch(`${config.RAG_API}/notes/${noteId}`, {
        method: "DELETE",
        body: { user_id: session?.user?.userId },
    });

    // 2. 刪除 Spring Boot rag_notes 紀錄
    await $fetch(`${config.RESOURCE_API}/rag-notes/${noteId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${session?.token}` },
    });

    return { deleted: true, noteId };
});

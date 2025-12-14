export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const query = getQuery(event);
    const notebookId = event.context.params?.id;
    console.log("----------");
    console.log(notebookId);

    // TODO(Forward): 這裡自動 forward 到 BE，可依照架構調整 path
    const config = useRuntimeConfig();
    return await $fetch(
        `${config.RESOURCE_API}/notebooks/${notebookId}/notes`,
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${session?.token}`,
            },
            query,
        }
    );
});

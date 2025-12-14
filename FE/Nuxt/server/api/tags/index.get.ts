export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const query = getQuery(event);

    // TODO(Forward): 這裡自動 forward 到 BE，可依照架構調整 path
    const config = useRuntimeConfig();
    return await $fetch(`${config.RESOURCE_API}/tags`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
        query,
    });
});

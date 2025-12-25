export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const query = getQuery(event);

    const config = useRuntimeConfig();
    return await $fetch(`${config.RESOURCE_API}/tags`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
        query,
    });
});

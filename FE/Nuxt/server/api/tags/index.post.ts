export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const body = await readBody(event);

    const config = useRuntimeConfig();
    return await $fetch(`${config.RESOURCE_API}/tags`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
        body,
    });
});

export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const body = await readBody(event);

    // TODO(Forward): 這裡自動 forward 到 BE，可依照架構調整 path
    const config = useRuntimeConfig();
    return await $fetch(`${config.RESOURCE_API}/tags`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
        body,
    });
});

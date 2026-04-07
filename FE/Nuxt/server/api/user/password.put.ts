export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const config = useRuntimeConfig();
    const body = await readBody(event);
    const userId = session?.user?.userId;

    return await $fetch(`${config.AUTH_API}/users/${userId}/password`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
        body,
    });
});

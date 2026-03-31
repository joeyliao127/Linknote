export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const config = useRuntimeConfig();

    return await $fetch(`${config.AUTH_API}/users/token`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
    });
});

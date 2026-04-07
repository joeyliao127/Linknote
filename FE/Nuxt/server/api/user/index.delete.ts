export default defineEventHandler(async (event) => {
    const session = event.context.session;
    const config = useRuntimeConfig();
    const userId = session?.user?.userId;

    await $fetch(`${config.AUTH_API}/users/${userId}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${session?.token}`,
        },
    });

    return { success: true };
});

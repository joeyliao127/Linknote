export default defineEventHandler(() => {
    throw createError({ statusCode: 403, statusMessage: "Forbidden" });
});

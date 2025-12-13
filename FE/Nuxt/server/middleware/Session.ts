// server/middleware/session.ts
export default defineEventHandler((event) => {
    const config = useRuntimeConfig();

    const sessionId = getCookie(event, config.public.SESSION_COOKIE);

    if (!sessionId) {
        // 無 session（例如：登入前的 API）
        event.context.session = null;
        createError({
            statusCode: 401,
        });
        return;
    }

    // 2. 從 global.sessionStorage 取得 session

    const sessionRaw = global.sessionStorage.getItem(sessionId);

    if (!sessionRaw) {
        event.context.session = null;
        return;
    }

    // 3. 解析 JSON（需要 try-catch 避免意外格式）
    try {
        const session = JSON.parse(sessionRaw);

        event.context.session = session;

        // Debug（可移除）
        // console.log("BFF session loaded:", session);
    } catch (err) {
        console.error("❌ session JSON parse error:", err);
        event.context.session = null;
        // createError()
    }
});

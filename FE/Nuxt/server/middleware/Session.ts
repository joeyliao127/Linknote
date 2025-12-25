import { useThrowApiError } from "../composables/useThrowApiError";
import { ErrorCode, ErrorCodes } from "../error/ErrorCode";

export default defineEventHandler(async (event) => {
    const config = useRuntimeConfig();

    const sessionId = getCookie(event, config.public.SESSION_COOKIE);

    if (!sessionId) {
        // 無 session（例如：登入前的 API
        useThrowApiError(ErrorCodes.UNAUTHORIZED);
    }

    try {
        const session = await useStorage("redis").getItem(sessionId as string);
        if (!session) {
            event.context.session = null;
            return;
        }
        event.context.session = session;

        // Debug（可移除）
        // console.log("BFF session loaded:", session);
    } catch (err) {
        console.error("❌ get session error:", err);
        event.context.session = null;
    }
});

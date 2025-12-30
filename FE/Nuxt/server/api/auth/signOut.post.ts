import { defineEventHandler, setCookie } from "h3";

const SESSION_COOKIE = "ln_auth_session";

export default defineEventHandler(async (event) => {
    const config = useRuntimeConfig();
    const sessionId = getCookie(event, config.public.SESSION_COOKIE);

    await useStorage("redis").removeItem(sessionId as string);
    return { result: true };
});

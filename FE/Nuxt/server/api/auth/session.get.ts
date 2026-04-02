import { defineEventHandler, getCookie } from "h3";

const SESSION_COOKIE = "ln_auth_session";

export default defineEventHandler(async (event) => {
    const raw = getCookie(event, SESSION_COOKIE);

    const session = await useStorage("redis").getItem(raw || "");
    if (!session) {
        return { user: null, token: null };
    }

    try {
        return typeof session === "string" ? JSON.parse(session) : session;
    } catch {
        return { user: null, token: null };
    }
});

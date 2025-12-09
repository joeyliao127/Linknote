import { defineEventHandler, getCookie } from "h3";

const SESSION_COOKIE = "ln_auth_session";

export default defineEventHandler(async (event) => {
    const raw = getCookie(event, SESSION_COOKIE);

    const session = global.sessionStorage.getItem(raw || "");
    if (!session) {
        return { user: null, token: null };
    }

    try {
        return JSON.parse(session);
    } catch {
        return { user: null, token: null };
    }
});

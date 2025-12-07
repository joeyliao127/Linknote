import { defineEventHandler, getCookie } from "h3";

const SESSION_COOKIE = "ln_auth_session";

export default defineEventHandler(async (event) => {
    const raw = getCookie(event, SESSION_COOKIE);

    if (!raw) {
        return { user: null, token: null };
    }

    try {
        return JSON.parse(raw);
    } catch {
        return { user: null, token: null };
    }
});

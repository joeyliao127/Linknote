import { defineEventHandler, setCookie } from "h3";

const SESSION_COOKIE = "ln_auth_session";

export default defineEventHandler(async (event) => {
    setCookie(event, SESSION_COOKIE, "", {
        httpOnly: true,
        sameSite: "lax",
        path: "/",
        secure: process.env.NODE_ENV === "production",
        maxAge: 0,
    });

    return { result: true };
});

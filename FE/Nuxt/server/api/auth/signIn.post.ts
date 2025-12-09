import { defineEventHandler, readBody, createError, setCookie } from "h3";

const SESSION_COOKIE = "ln_auth_session";

export default defineEventHandler(async (event) => {
    const body = await readBody<{
        email?: string;
        password?: string;
    }>(event);

    const email = body?.email;
    const password = body?.password;

    if (!email || !password) {
        throw createError({
            statusCode: 400,
            statusMessage: "缺少 email 或 password",
        });
    }

    const runtimeConfig = useRuntimeConfig();
    const apiBase = runtimeConfig.AUTH_API;

    try {
        const response = await $fetch<{ token?: string; userId?: string }>(
            `${apiBase}/users/signIn`,
            {
                method: "POST",
                body: { email, password },
            }
        );

        if (!response?.token || !response?.userId) {
            throw createError({
                statusCode: 502,
                statusMessage: "後端登入回應不完整",
            });
        }

        const session = {
            token: response.token,
            user: {
                userId: response.userId,
                email,
            },
        };
        const sessionId = crypto.randomUUID();

        // TODO: 改成用 Redis 或其他方式儲存 session，實現 Serverless Session
        global.sessionStorage.setItem(sessionId, JSON.stringify(session));

        setCookie(event, SESSION_COOKIE, JSON.stringify(session), {
            httpOnly: true,
            sameSite: "lax",
            path: "/",
            secure: process.env.NODE_ENV === "production",
            maxAge: 60 * 60 * 24 * 7, // 7 天
        });

        return session;
    } catch (error: any) {
        if (error?.statusCode) {
            throw error;
        }

        const statusCode = error?.response?.status || error?.statusCode || 500;
        const statusMessage =
            error?.data?.message || error?.message || "登入失敗，請稍後再試";

        throw createError({
            statusCode,
            statusMessage,
        });
    }
});

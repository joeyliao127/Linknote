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
    const authApiBase =
        runtimeConfig.authApiBase ||
        runtimeConfig.public.AUTH_API_BASE ||
        runtimeConfig.public.API_URL;

    if (!authApiBase) {
        throw createError({
            statusCode: 500,
            statusMessage: "未設定後端 API base（AUTH_API_BASE 或 API_URL）",
        });
    }

    const apiBase = authApiBase.replace(/\/$/, "");
    try {
        const res = await $fetch<{ token?: string; userId?: string }>(
            `${apiBase}/users/signIn`,
            {
                method: "POST",
                body: { email, password },
            }
        );

        if (!res?.token || !res?.userId) {
            throw createError({
                statusCode: 502,
                statusMessage: "後端登入回應不完整",
            });
        }

        const session = {
            token: res.token,
            user: {
                userId: res.userId,
                email,
            },
        };

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

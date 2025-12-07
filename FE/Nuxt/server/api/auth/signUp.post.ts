import { defineEventHandler, readBody, createError } from "h3";

export default defineEventHandler(async (event) => {
    const body = await readBody<{
        email?: string;
        password?: string;
        username?: string;
    }>(event);

    const email = body?.email;
    const password = body?.password;
    const username = body?.username;

    if (!email || !password || !username) {
        throw createError({
            statusCode: 400,
            statusMessage: "缺少 email、password 或 username",
        });
    }

    const runtimeConfig = useRuntimeConfig();
    const rawBase =
        runtimeConfig.authApiBase ||
        runtimeConfig.public.AUTH_API_BASE ||
        runtimeConfig.public.API_URL;

    const authApiBase = rawBase?.startsWith("http")
        ? rawBase
        : `http://${rawBase || "springboot"}:${
              process.env.BACKEND_PORT || 8080
          }/api`;

    if (!authApiBase) {
        throw createError({
            statusCode: 500,
            statusMessage: "未設定後端 API base（AUTH_API_BASE 或 API_URL）",
        });
    }

    const apiBase = authApiBase.replace(/\/$/, "");

    try {
        await $fetch(`${apiBase}/users/signUp`, {
            method: "POST",
            body: { email, password, username },
        });

        return { result: true, message: "註冊成功" };
    } catch (error: any) {
        const statusCode =
            error?.response?.status || error?.statusCode || 500;
        const statusMessage =
            error?.data?.message || error?.message || "註冊失敗，請稍後再試";

        throw createError({
            statusCode,
            statusMessage,
        });
    }
});

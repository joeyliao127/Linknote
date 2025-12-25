import { defineNuxtConfig } from "nuxt/config";
import tailwindcss from "@tailwindcss/vite";

const redisPort = Number.parseInt(process.env.REDIS_PORT || "6379", 10) || 6379;
const redisDb = Number.parseInt(process.env.REDIS_DB || "0", 10) || 0;

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    compatibilityDate: "2025-05-15",
    runtimeConfig: {
        // 兩個 BE API Server
        AUTH_API: process.env.AUTH_API_BASE,
        RESOURCE_API: process.env.RESOURCE_API_BASE,
        public: {
            API_URL: process.env.API_URL,
            SESSION_COOKIE: "ln_auth_session",
        },
    },

    css: ["~/assets/css/tailwind.css"],

    imports: {
        // 讓 Nuxt scan 所有層級的 composable 都套用 auto import 功能
        dirs: ["~/composables", "~/composables/**"],
    },

    typescript: {
        strict: true,
        typeCheck: "build",
    },

    devtools: { enabled: true },
    modules: [
        "@nuxtjs/i18n",
        "@nuxt/ui",
        "@nuxt/icon",
        "nuxt-lucide-icons",
        "@sidebase/nuxt-auth",
    ],

    /**
     * Nuxt-Auth local backedn 文件：https://auth.sidebase.io/guide/local/quick-start
     * 1. type 設定 local
     * 2. endpoints 設定對應的 API 路徑，需要在 server/api/auth/ 下實作對應的 API，透過 Nuxt server 呼叫 Spring Boot API
     */
    auth: {
        provider: {
            type: "local",
            endpoints: {
                signIn: { path: "/signIn", method: "post" },
                signOut: { path: "/signOut", method: "post" },
                signUp: { path: "/signUp", method: "post" },
                getSession: { path: "/session", method: "get" },
            },
        },
    },
    i18n: {
        defaultLocale: "zh",
        locales: [
            { code: "en", language: "en-US" },
            { code: "zh", language: "zh-TW" },
        ],
        bundle: {
            optimizeTranslationDirective: false,
        },
    },

    vite: {
        plugins: [tailwindcss()],
        server: {
            // TODO: Domain 改為 project name
            allowedHosts: ["nuxt", "linknote.local.com"],
        },
        hmr: {
            protocol: "ws",
            host: "linknote.local.com",
            clientPort: 443, // 如果你沒用 HTTPS 可改成 80
        },
    },

    ui: {
        theme: {
            // tailwind 原本就支援 primary, secondary，若要套用 color 到 UButton，可以在此設定
            colors: ["accent"],
        },
    },

    //icon設定
    lucide: {
        namePrefix: "lucide",
    },

    alias: {},

    nitro: {
        externals: {
            inline: ["next-auth"],
        },
        storage: {
            redis: {
                driver: "redis",
                host: "redis",
                port: redisPort,
                db: 0, // Defaults to 0
            },
        },
    },
});

import { defineNuxtConfig } from "nuxt/config";
import tailwindcss from "@tailwindcss/vite";

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    compatibilityDate: "2025-05-15",
    runtimeConfig: {
        public: {
            API_URL: process.env.API_URL,
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
    modules: ["@nuxtjs/i18n", "@nuxt/ui", "@nuxt/icon", "nuxt-lucide-icons"],

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
});

export default defineAppConfig({
    WEBSITE_NAME: "Linknote",
    WEBSITE_TITLE: "Linknote",
    WEBSITE_DESC: "Linknote",

    ui: {
        button: {
            // 覆寫預設樣式，rounded 對應 tailwind.css 中 --radius: var(--ui-radius)
            slots: {
                base: ["justify-center font-bold rounded "],
            },
        },
        popover: {
            slots: {
                content: "!bg-[#363636]/80 backdrop-blur-sm !ring-0 border border-white/10 rounded-lg shadow-xl",
            },
        },
        card: {
            slots: {
                root: "!ring-0 shadow-none !bg-black/35",
            },
        },
        input: {
            slots: {
                root: "!ring-0 !shadow-none border border-white/30 rounded focus-within:border-white/60 transition-colors w-full",
                base: "!bg-transparent !ring-0 !outline-none !shadow-none text-white placeholder:text-white/40",
            },
        },
        textarea: {
            slots: {
                root: "!ring-0 !shadow-none border border-white/30 rounded focus-within:border-white/60 transition-colors w-full",
                base: "!bg-transparent !ring-0 !outline-none !shadow-none text-white placeholder:text-white/40",
            },
        },
        formField: {
            slots: {
                label: "font-medium",
                error: "text-red-600",
                required: "after:content-['*'] after:ml-0.5 after:text-red-600",
            },
            required: {
                true: {
                    label: "after:content-['*'] after:ms-0.5 after:text-red-600",
                },
            },
        },
    },
});

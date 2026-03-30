export default defineAppConfig({
    WEBSITE_NAME: "Linknote",
    WEBSITE_TITLE: "Linknote",
    WEBSITE_DESC: "Linknote",

    ui: {
        button: {
            // 覆寫預設樣式
            slots: {
                base: ["justify-center font-bold"],
            },
        },
        input: {
            slots: {
                root: "ring-0 shadow-none",
                base: "ring-0 border-0 outline-none focus:ring-0 focus-visible:ring-0 shadow-none",
            },
        },
        formField: {
            slots: {
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

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
        formField: {
            slots: {
                error: "text-red-500",
                required: "after:content-['*'] after:ml-0.5 after:text-red-500",
            },
            required: {
                true: {
                    label: "after:content-['*'] after:ms-0.5 after:text-red-500",
                },
            },
        },
    },
});

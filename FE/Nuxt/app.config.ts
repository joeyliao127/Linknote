import { defineAppConfig } from "#app";

export default defineAppConfig({
    WEBSITE_NAME: "Linknote",
    WEBSITE_TITLE: "Linknote",
    WEBSITE_DESC: "Linknote",

    ui: {
        button: {
            // 覆寫預設樣式
            slots: {
                base: ["justify-center !text-white font-bold"],
            },
        },
    },
});

// composables/useDialogs.ts
import { ref } from "vue";

export type DialogType = "alert" | "confirm" | "inform";

export interface DialogOptions {
    type: DialogType;
    title?: string;
    message: string;
    callback?: () => void | Promise<void>;
}

interface DialogState extends DialogOptions {
    id: string;
    isOpen: boolean;
}

const dialogs = ref<DialogState[]>([]);

export const useDialogs = () => {
    const show = (options: DialogOptions) => {
        const id = `dialog-${Date.now()}-${Math.random()}`;
        const dialog: DialogState = {
            ...options,
            id,
            isOpen: true,
        };
        dialogs.value.push(dialog);
        return id;
    };

    const close = (id: string) => {
        const dialog = dialogs.value.find((d) => d.id === id);
        if (dialog) {
            dialog.isOpen = false;
            setTimeout(() => {
                dialogs.value = dialogs.value.filter((d) => d.id !== id);
            }, 300);
        }
    };

    const alert = (
        message: string,
        title?: string,
        callback?: () => void | Promise<void>
    ) => {
        return show({
            type: "alert",
            title: title || "警告",
            message,
            callback,
        });
    };

    const confirm = (
        message: string,
        title?: string,
        callback?: () => void | Promise<void>
    ) => {
        return show({
            type: "confirm",
            title: title || "確認",
            message,
            callback,
        });
    };

    const inform = (
        message: string,
        title?: string,
        callback?: () => void | Promise<void>
    ) => {
        return show({
            type: "inform",
            title: title || "通知",
            message,
            callback,
        });
    };

    return {
        dialogs,
        alert,
        confirm,
        inform,
        close,
    };
};

<template>
    <ClientOnly>
        <!-- Alert Dialog -->
        <UModal
            v-for="dialog in alertDialogs"
            :key="dialog.id"
            v-model:open="dialog.isOpen"
            :ui="UI_DIALOG"
            @close="handleClose(dialog.id)">
            <template #header></template>
            <template #body>
                <div class="flex items-center gap-3 px-5 pt-5 pb-4">
                    <UIcon
                        name="i-lucide-triangle-alert"
                        class="w-6 h-6 text-yellow-400 shrink-0" />
                    <span
                        class="text-white font-medium text-base leading-snug"
                        v-html="dialog.message"></span>
                </div>
                <div class="flex justify-center gap-x-4 px-5 pb-5">
                    <UButton
                        color="alert"
                        class="min-w-24 h-10 text-white font-medium"
                        @click="handleConfirm(dialog.id, dialog.callback)">
                        確定
                    </UButton>
                </div>
            </template>
        </UModal>

        <!-- Confirm Dialog -->
        <UModal
            v-for="dialog in confirmDialogs"
            :key="dialog.id"
            v-model:open="dialog.isOpen"
            :ui="UI_DIALOG"
            @close="handleClose(dialog.id)">
            <template #header></template>
            <template #body>
                <div class="flex items-center gap-3 pb-4">
                    <UIcon
                        name="i-lucide-triangle-alert"
                        class="w-6 h-6 text-yellow-400 shrink-0" />
                    <span
                        class="text-white font-medium text-base leading-snug"
                        v-html="dialog.message"></span>
                </div>
                <div class="flex justify-center gap-x-4">
                    <UButton
                        color="alert"
                        class="min-w-24 h-10 text-white font-medium"
                        @click="handleConfirm(dialog.id, dialog.callback)">
                        確認
                    </UButton>
                    <UButton
                        variant="ghost"
                        color="neutral"
                        class="min-w-24 h-10 border border-white/20 text-white font-medium hover:bg-white/10"
                        @click="handleClose(dialog.id)">
                        取消
                    </UButton>
                </div>
            </template>
        </UModal>

        <!-- Inform Dialog -->
        <UModal
            v-for="dialog in informDialogs"
            :key="dialog.id"
            v-model:open="dialog.isOpen"
            :ui="UI_DIALOG"
            @close="handleClose(dialog.id)">
            <template #header></template>
            <template #body>
                <div class="flex items-center gap-3 px-5 pt-5 pb-4">
                    <UIcon
                        name="i-lucide-info"
                        class="w-6 h-6 text-blue-400 shrink-0" />
                    <span
                        class="text-white font-medium text-base leading-snug"
                        v-html="dialog.message"></span>
                </div>
                <div class="flex justify-center gap-x-4 px-5 pb-5">
                    <UButton
                        color="primary"
                        class="min-w-24 h-10 text-white font-medium"
                        @click="handleConfirm(dialog.id, dialog.callback)">
                        知道了
                    </UButton>
                </div>
            </template>
        </UModal>
    </ClientOnly>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useDialogs } from "~/composables/useDialogs";

const { dialogs, close } = useDialogs();

const UI_DIALOG = {
    content:
        "rounded-sm border border-white/10 bg-[#363636]/80 backdrop-blur-sm max-w-120 shadow-xl",
    header: "hidden",
    body: "p-0",
    overlay: "bg-black/50",
};

// Filter dialogs by type
const alertDialogs = computed(() =>
    dialogs.value.filter((d) => d.type === "alert"),
);
const confirmDialogs = computed(() =>
    dialogs.value.filter((d) => d.type === "confirm"),
);
const informDialogs = computed(() =>
    dialogs.value.filter((d) => d.type === "inform"),
);

const handleClose = (id: string) => {
    close(id);
};

const handleConfirm = async (
    id: string,
    callback?: () => void | Promise<void>,
) => {
    if (callback) {
        await callback();
    }
    close(id);
};
</script>

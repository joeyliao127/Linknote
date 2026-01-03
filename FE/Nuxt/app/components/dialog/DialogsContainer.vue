<template>
    <ClientOnly>
        <!-- Alert Dialog -->
        <UModal
            v-for="dialog in alertDialogs"
            :key="dialog.id"
            v-model:open="dialog.isOpen"
            :title="dialog.title || '品學堂'"
            :close="UI_CLOSE"
            :ui="UI_MODAL"
            @close="handleClose(dialog.id)">
            <template #description></template>
            <template #body>
                <div
                    class="mx-auto mb-6 border-b border-gray-900 lg:w-3/4"></div>
                <div
                    class="text-error mb-4 text-center text-lg leading-normal font-bold tracking-widest"
                    v-html="dialog.message"></div>
            </template>
            <template #footer>
                <UButton
                    class="min-w-75 rounded-full"
                    color="error"
                    @click="handleConfirm(dialog.id, dialog.callback)">
                    確定
                </UButton>
            </template>
        </UModal>

        <!-- Confirm Dialog -->
        <UModal
            v-for="dialog in confirmDialogs"
            :key="dialog.id"
            v-model:open="dialog.isOpen"
            :title="dialog.title || '品學堂'"
            :close="UI_CLOSE"
            :ui="UI_MODAL"
            @close="handleClose(dialog.id)">
            <template #description></template>
            <template #body>
                <div
                    class="mx-auto mb-6 border-b border-gray-900 lg:w-3/4"></div>
                <div
                    class="mb-4 text-center text-lg leading-normal font-bold tracking-widest"
                    v-html="dialog.message"></div>
            </template>
            <template #footer>
                <UButton
                    class="min-w-40 rounded-full"
                    @click="handleConfirm(dialog.id, dialog.callback)">
                    確認
                </UButton>
                <UButton
                    class="min-w-40 rounded-full"
                    variant="outline"
                    @click="handleClose(dialog.id)">
                    取消
                </UButton>
            </template>
        </UModal>

        <!-- Inform Dialog -->
        <UModal
            v-for="dialog in informDialogs"
            :key="dialog.id"
            v-model:open="dialog.isOpen"
            :title="dialog.title || '品學堂'"
            :close="UI_CLOSE"
            :ui="UI_MODAL"
            @close="handleClose(dialog.id)">
            <template #description></template>
            <template #body>
                <div
                    class="mx-auto mb-6 border-b border-gray-900 lg:w-3/4"></div>
                <div
                    class="mb-4 text-center text-lg leading-normal font-bold tracking-widest"
                    v-html="dialog.message"></div>
            </template>
            <template #footer>
                <UButton
                    class="min-w-75 rounded-full"
                    color="info"
                    @click="handleConfirm(dialog.id, dialog.callback)">
                    知道了
                </UButton>
            </template>
        </UModal>
    </ClientOnly>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useDialogs } from "~/composables/useDialogs";

const { dialogs, close } = useDialogs();

// Modify the default theme of NuxtUI Modal
const UI_MODAL = {
    header: "text-2xl pt-12 font-bold border-0 text-center justify-center pb-0",
    wrapper: "border-0",
    content: "rounded-none ring-none border border-0.5 border-black bg-default",
    body: "border-0 pt-0 text-lg md:text-base",
    footer: "items-center flex-col md:flex-row md:justify-center",
    overlay: "bg-black/30",
};

const UI_CLOSE = {
    variant: "link",
    class: "text-black button_close !top-2 !end-2",
};

// Filter dialogs by type
const alertDialogs = computed(() =>
    dialogs.value.filter((d) => d.type === "alert")
);
const confirmDialogs = computed(() =>
    dialogs.value.filter((d) => d.type === "confirm")
);
const informDialogs = computed(() =>
    dialogs.value.filter((d) => d.type === "inform")
);

const handleClose = (id: string) => {
    close(id);
};

const handleConfirm = async (
    id: string,
    callback?: () => void | Promise<void>
) => {
    if (callback) {
        await callback();
    }
    close(id);
};
</script>

<style>
@reference "~/assets/css/tailwind.css";

button.button_close {
    span.iconify {
        @apply size-6;
    }
}
</style>

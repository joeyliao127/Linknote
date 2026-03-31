<template>
    <UModal
        :model-value="open"
        :title="title || '確認'"
        @update:model-value="$emit('update:open', $event)">
        <template #header>
            <div class="text-lg font-semibold">{{ title || '確認' }}</div>
        </template>

        <p class="text-sm text-slate-300">{{ message }}</p>

        <template #footer>
            <div class="flex gap-2 justify-end">
                <UButton
                    variant="ghost"
                    @click="$emit('update:open', false)">
                    取消
                </UButton>
                <UButton
                    :loading="loading"
                    color="accent"
                    @click="handleConfirm">
                    {{ confirmText || '確定刪除' }}
                </UButton>
            </div>
        </template>
    </UModal>
</template>

<script setup lang="ts">
const props = withDefaults(
    defineProps<{
        open: boolean;
        title?: string;
        message: string;
        confirmText?: string;
        loading?: boolean;
    }>(),
    {
        loading: false,
    },
);

const emit = defineEmits<{
    (e: "update:open", value: boolean): void;
    (e: "confirm"): void;
}>();

function handleConfirm() {
    emit("confirm");
}
</script>

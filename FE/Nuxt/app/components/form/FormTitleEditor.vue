<template>
    <div class="flex rounded-md hover:bg-muted">
        <!-- 顯示模式 -->
        <div
            v-if="!isEditing"
            @click="startEdit"
            class="cursor-pointer px-2.5 py-1.5 transition-colors font-semibold">
            {{ model || "點擊輸入文字" }}
        </div>

        <!-- 編輯模式 -->
        <UInput
            v-else
            ref="input"
            v-model="model"
            @blur="handleBlur"
            @keydown="handleKeydown"
            @change="change" />
        <UButton color="gray" variant="ghost" size="sm" icon="i-lucide-pen" />
    </div>
</template>

<script setup lang="ts">
// const props = defineProps<{ title: string }>();
const model = defineModel();
const emits = defineEmits(["change"]);

const isEditing = ref(false);
const text = ref("點擊我來編輯");
const inputRef = useTemplateRef("input");

function startEdit() {
    isEditing.value = true;
    nextTick(() => {
        inputRef.value?.inputRef?.focus();
        inputRef.value?.inputRef?.select();
    });
}

function finishEdit() {
    isEditing.value = false;
}

function handleBlur() {
    finishEdit();
}

function handleKeydown(e: KeyboardEvent) {
    if (e.key === "Enter") {
        finishEdit();
    }
    if (e.key === "Escape") {
        isEditing.value = false;
    }
}

function change() {
    emits("change");
}
</script>

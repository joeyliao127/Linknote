<template>
    <ResizablePanel
        orientation="horizontal"
        :default-size="defaultHeight"
        :min-size="minHeight"
        :max-size="maxHeight"
        :storage-key="storageKey"
        :resizable="resizable"
        class="flex flex-col border border-slate-700 bg-slate-900">
        <!-- Header -->
        <div class="p-3 border-b border-slate-700">
            <h3 class="text-sm font-semibold text-slate-100">{{ title }}</h3>
            <p v-if="subtitle" class="text-xs text-slate-400 mt-1">
                {{ subtitle }}
            </p>
        </div>

        <!-- Editor Content -->
        <div class="flex-1 overflow-hidden p-2 h-full">
            <UEditor
                v-model="content"
                class="h-screen cursor-text"
                :placeholder="placeholder"
                @update:model-value="handleUpdate" />
        </div>
    </ResizablePanel>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import ResizablePanel from "../ui/ResizablePanel.vue";

interface Props {
    modelValue?: string;
    title: string;
    subtitle?: string;
    placeholder?: string;
    defaultHeight?: number;
    minHeight?: number;
    maxHeight?: number;
    storageKey?: string;
    resizable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    modelValue: "",
    defaultHeight: 300,
    minHeight: 150,
    maxHeight: 800,
    resizable: true,
    placeholder: "開始輸入...",
});

const emit = defineEmits<{
    "update:modelValue": [value: string];
    change: [value: string];
}>();

const content = ref(props.modelValue || "");

watch(
    () => props.modelValue,
    (newValue) => {
        content.value = newValue || "";
    }
);

const handleUpdate = (value: string) => {
    emit("update:modelValue", value);
    emit("change", value);
};
</script>

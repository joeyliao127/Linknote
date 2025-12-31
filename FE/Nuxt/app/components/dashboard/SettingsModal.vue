<template>
    <div>
        <UModal v-model="open" :ui="modalUi">
            <UButton
                block
                variant="ghost"
                icon="i-lucide-settings-2"
                class="justify-start"
                @click="openModal">
                設定
            </UButton>
            <template #content>
                <div
                    class="flex w-full max-h-[80vh] aspect-[4/3] rounded-2xl border border-slate-800 bg-slate-950 text-slate-50 shadow-2xl overflow-hidden">
                    <div
                        class="w-48 border-r border-slate-800 p-3 space-y-1 bg-slate-900/70">
                        <template v-for="item in sections" :key="item.value">
                            <UButton
                                block
                                :variant="activeSection === item.value ? 'soft' : 'ghost'"
                                :color="activeSection === item.value ? 'accent' : 'neutral'"
                                class="justify-start"
                                @click="changeSection(item.value)">
                                <UIcon :name="item.icon || 'i-lucide-circle'" class="w-4 h-4 mr-2" />
                                {{ item.label }}
                            </UButton>
                        </template>
                    </div>
                    <div class="flex-1 p-5 space-y-4">
                        <div class="flex items-start justify-between">
                            <div class="space-y-1">
                                <p class="text-lg font-semibold">
                                    {{
                                        sections.find(
                                            (tab) => tab.value === activeSection
                                        )?.label
                                    }}
                                </p>
                                <p class="text-sm text-slate-400">
                                    {{
                                        sections.find(
                                            (tab) => tab.value === activeSection
                                        )?.description || "設定細項"
                                    }}
                                </p>
                            </div>
                            <UButton
                                variant="ghost"
                                color="neutral"
                                icon="i-lucide-x"
                                size="sm"
                                @click="closeModal" />
                        </div>

                        <div class="space-y-3">
                            <slot name="content" :section="activeSection">
                                <p class="text-sm text-slate-400">
                                    此處預留設定表單內容（由外部注入）。
                                </p>
                            </slot>
                        </div>

                        <div
                            class="pt-4 border-t border-slate-800 flex flex-wrap gap-2">
                            <slot name="footer" :close="closeModal" />
                        </div>
                    </div>
                </div>
            </template>
        </UModal>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";

export interface SettingsSection {
    label: string;
    value: string;
    description?: string;
    icon?: string;
}

const props = withDefaults(
    defineProps<{
        modelValue?: boolean;
        sections?: SettingsSection[];
        active?: string;
    }>(),
    {
        modelValue: false,
        sections: () => [
            { label: "個人資料", value: "profile", icon: "i-lucide-user" },
            { label: "帳號安全", value: "security", icon: "i-lucide-lock" },
            { label: "通知", value: "notification", icon: "i-lucide-bell" },
        ],
        active: "",
    }
);

const emit = defineEmits<{
    (e: "update:modelValue", value: boolean): void;
    (e: "change", value: string): void;
    (e: "close"): void;
}>();

const open = computed({
    get: () => props.modelValue,
    set: (value: boolean) => emit("update:modelValue", value),
});

const activeSection = ref(props.active || props.sections[0]?.value || "");

watch(
    () => props.active,
    (val) => {
        if (val) activeSection.value = val;
    }
);

function changeSection(value: string) {
    activeSection.value = value;
    emit("change", value);
}

function openModal() {
    emit("update:modelValue", true);
}

function closeModal() {
    emit("update:modelValue", false);
    emit("close");
}

const modalUi = {
    overlay: { base: "fixed inset-0 bg-black/50 backdrop-blur" },
    container: "fixed inset-0 flex items-center justify-center p-4",
    base: "w-[70vw] max-w-[770px]",
};
</script>

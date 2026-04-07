<template>
    <UForm
        :state="formState"
        :schema="schema"
        class="flex flex-col h-full gap-4"
        @submit="onSubmit">
        <UFormField :label="$t('components.notebookForm.nameLabel')" name="title" required>
            <UInput
                v-model="formState.title"
                :placeholder="$t('components.notebookForm.namePlaceholder')"
                icon="i-lucide-notebook-pen"
                class="w-full" />
        </UFormField>

        <UFormField :label="$t('components.notebookForm.descLabel')" name="description" class="flex flex-col flex-1 min-h-0">
            <UTextarea
                v-model="formState.description"
                :placeholder="$t('components.notebookForm.descPlaceholder')"
                autoresize
                class="w-full h-full"
                :ui="{ base: 'resize-none h-full' }" />
        </UFormField>

        <div class="flex items-center justify-between">
            <label class="text-sm text-slate-300">{{ $t('components.notebookForm.active') }}</label>
            <USwitch v-model="formState.active" />
        </div>

        <UButton
            type="submit"
            block
            color="primary"
            class="text-white"
            :loading="submitting"
            icon="i-lucide-save">
            {{ submitLabel || $t('components.notebookForm.defaultSubmit') }}
        </UButton>
    </UForm>
</template>

<script setup lang="ts">
import * as z from "zod";
import { computed, reactive, watch } from "vue";
import type { FormSubmitEvent } from "@nuxt/ui";
import type { CreateNotebookDTO } from "~~/types/Notebook";

const { t } = useI18n();

const props = withDefaults(
    defineProps<{
        initialValue?: Partial<CreateNotebookDTO> & { active?: boolean };
        submitting?: boolean;
        submitLabel?: string;
    }>(),
    {
        initialValue: () => ({ active: true }),
        submitting: false,
        submitLabel: "",
    },
);

const emit = defineEmits<{
    (
        e: "submit",
        value: { title: string; description?: string; active?: boolean },
    ): void;
}>();

const schema = computed(() =>
    z.object({
        title: z.string().nonempty(t('components.notebookForm.nameRequired')),
        description: z.string().optional(),
        active: z.boolean().default(true),
    }),
);

const formState = reactive({
    title: props.initialValue.title ?? "",
    description: props.initialValue.description ?? "",
    active: props.initialValue.active ?? true,
});

watch(
    () => props.initialValue,
    (value) => {
        formState.title = value?.title ?? "";
        formState.description = value?.description ?? "";
        formState.active = value?.active ?? true;
    },
    { deep: true },
);

function onSubmit(event: FormSubmitEvent<any>) {
    emit("submit", event.data);
}
</script>

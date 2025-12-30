<template>
    <UForm
        :state="formState"
        :schema="schema"
        class="space-y-4"
        @submit="onSubmit">
        <UFormField label="名稱" name="title" required>
            <UInput
                v-model="formState.title"
                placeholder="輸入筆記本名稱"
                icon="i-lucide-notebook-pen" />
        </UFormField>

        <UFormField label="描述" name="description">
            <UTextarea
                v-model="formState.description"
                placeholder="描述這本筆記本的用途"
                :rows="3" />
        </UFormField>

        <div class="flex items-center justify-between">
            <label class="text-sm text-slate-300">啟用</label>
            <USwitch v-model="formState.active" />
        </div>

        <UButton
            type="submit"
            block
            color="accent"
            :loading="submitting"
            icon="i-lucide-save">
            {{ submitLabel }}
        </UButton>
    </UForm>
</template>

<script setup lang="ts">
import * as z from "zod";
import { reactive, watch } from "vue";
import type { FormSubmitEvent } from "@nuxt/ui";
import type { CreateNotebookDTO } from "~~/types/Notebook";

const props = withDefaults(
    defineProps<{
        initialValue?: Partial<CreateNotebookDTO> & { active?: boolean };
        submitting?: boolean;
        submitLabel?: string;
    }>(),
    {
        initialValue: () => ({ active: true }),
        submitting: false,
        submitLabel: "建立筆記本",
    }
);

const emit = defineEmits<{
    (e: "submit", value: { title: string; description?: string; active?: boolean }): void;
}>();

const schema = z.object({
    title: z.string().nonempty("筆記本名稱不能為空值"),
    description: z.string().optional(),
    active: z.boolean().default(true),
});

type NotebookFormSchema = z.output<typeof schema>;

const formState = reactive<NotebookFormSchema>({
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
    { deep: true }
);

function onSubmit(event: FormSubmitEvent<NotebookFormSchema>) {
    emit("submit", event.data);
}
</script>

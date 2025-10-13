<template>
    <div class="p-4 space-y-4">
        <h1>Notebooks</h1>
        <p>userId: {{ userId }}</p>
        <p>email: {{ email }}</p>
        <div class="flex gap-x-2">
            <nuxt-link to="/">
                <UButton>Home</UButton>
            </nuxt-link>

            <UButton @:click="clear">Clear</UButton>
        </div>

        <USeparator>
            <h2 class="title text-2xl">Notebooks</h2>
        </USeparator>
        <p>notebookId: {{ notebookId }}</p>

        <div class="grid grid-cols-6 gap-4">
            <template v-for="item in notebooks" :key="item.id">
                <UCard class="flex justify-center">
                    <nuxt-link :to="`/notebooks/${item.id}`" class="w-full">
                        <h3 class="font-semibold text-xl">{{ item.title }}</h3>
                    </nuxt-link>
                    <UInput
                        v-model="item.title"
                        label="Name"
                        type="text"
                        class="mt-4" />

                    <UButton
                        class="w-full mt-4"
                        @click="_deleteNotebook(item.id)">
                        刪除
                    </UButton>
                </UCard>
            </template>
        </div>
        <UCard class="w-100">
            <UForm
                :state="notebookState"
                :schema="notebookSchema"
                @submit="onNotebookSubmit"
                class="space-y-4">
                <h3 class="font-semibold">新增筆記本</h3>
                <UFormField label="title" name="title" required>
                    <UInput
                        v-model="notebookState.title"
                        type="text"
                        class="w-full" />
                </UFormField>
                <UFormField label="description" name="description">
                    <UInput
                        v-model="notebookState.description"
                        type="text"
                        class="w-full" />
                </UFormField>

                <UButton type="submit" class="w-full">Create</UButton>
            </UForm>
        </UCard>

        <USeparator>
            <h2 class="title text-2xl">Tags</h2>
        </USeparator>
        <UButton @click="getTags">getTags</UButton>

        <div class="flex gap-2">
            <template v-for="tag in tags">
                <UBadge>
                    {{ tag.title }}
                </UBadge>
            </template>
        </div>
    </div>
</template>
<script setup lang="ts">
import * as z from "zod";
import { useNotebook } from "~/composables/notebook/useNotebook";
import { useTag } from "~/composables/tag/useTag";

import type { FormSubmitEvent } from "@nuxt/ui";
import type { Notebook } from "~/types/Notebook";
import type { Note } from "~/types/Note";
import type { Tag } from "~/types/Tag";
import type { Pagination } from "~/types";
import type { ApiError, ApiResult } from "~/types/common";

const { indexNotebook, createNotebook, updateNotebook, deleteNotebook } =
    useNotebook();

const { indexTags, createTag, updateTag, deleteTag } = useTag();

const toast = useToast();

const userId = ref<string>("");
const email = ref<string>("");
const notebooks = ref<Notebook[]>([]);
const notes = ref<Note[]>([]);
const tags = ref<Tag[]>([]);

const notebookId = ref<string>("");

const notebookSchema = z.object({
    title: z.string().nonempty("筆記本名稱不能為空值"),
    description: z.string().optional(),
});

type CreateNotebookSchema = z.output<typeof notebookSchema>;

const notebookState = reactive<Partial<CreateNotebookSchema>>({
    title: "",
    description: "",
});

async function getNotebooks() {
    if (userId.value === null) {
        toast.add({
            title: "Auth error",
            description: `sign in first.`,

            color: "accent",
        });
        return;
    }

    const resNotebook: Pagination<Notebook> | ApiError = await indexNotebook(
        userId.value
    );

    notebooks.value = resNotebook.items;
}

async function _deleteNotebook(notebookId: string) {
    await deleteNotebook(userId.value, notebookId);

    toast.add({
        title: "Info",
        description: "Delete notebook success!",
        color: "primary",
    });

    getNotebooks();
}

async function onNotebookSubmit(event: FormSubmitEvent<CreateNotebookSchema>) {
    const { title, description } = event.data;
    await createNotebook(userId.value, { title, description });
    toast.add({
        title: "Success",
        description: "The form has been submitted.",
        color: "primary",
    });

    await getNotebooks();
}

async function getTags() {
    const res = await indexTags(userId.value);
    console.log(res);
    tags.value = res.items;
}

function clear() {
    localStorage.clear();
    navigateTo("/");
}

onMounted(async () => {
    const _userId = localStorage.getItem("userId");

    if (!_userId) {
        clear();
        navigateTo("/");
        return;
    }

    userId.value = _userId;

    if (!userId.value) {
        navigateTo("/");
    }

    await getNotebooks();
});
</script>

<style></style>

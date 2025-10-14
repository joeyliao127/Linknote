<template>
    <div class="p-4 space-y-4">
        <USeparator>
            <h2 class="title text-2xl">Notes</h2>
        </USeparator>

        <nuxt-link to="/notebooks">
            <UButton>To Notebook</UButton>
        </nuxt-link>

        <div class="grid grid-cols-3 gap-4">
            <template v-for="value in notes">
                <UCard>
                    <UForm
                        :state="value"
                        :schema="updateNoteSechema"
                        @submit="onUpdateNoteSubmit"
                        class="space-y-2">
                        <div class="flex flex-col">
                            <USwitch v-model="value.star" class="self-end" />
                            <UFormField label="id" name="id">
                                <UInput
                                    v-model="value.id"
                                    type="text"
                                    disabled
                                    class="w-full mt-4" />
                            </UFormField>
                        </div>

                        <UFormField label="title" name="title">
                            <UInput
                                v-model="value.title"
                                type="text"
                                class="w-full" />
                        </UFormField>
                        <UFormField label="keypoint" name="keypoint">
                            <UInput
                                v-model="value.keypoint"
                                type="text"
                                class="w-full" />
                        </UFormField>
                        <UFormField label="content" name="content">
                            <UInput
                                v-model="value.content"
                                type="text"
                                class="w-full" />
                        </UFormField>

                        <p>tags: {{ concatNotesTitle(value.tags) }}</p>

                        <div class="flex justify-between">
                            <UButton type="submit">更新</UButton>
                            <UButton @click="_deleteNote(value.id)">
                                刪除
                            </UButton>
                        </div>
                    </UForm>
                </UCard>
            </template>
            <UCard class="border-1 border-green-900">
                <UForm
                    :state="createNoteState"
                    :schema="createNoteSchema"
                    @submit="onCreateNoteSubmit"
                    class="space-y-4">
                    <div class="flex justify-between">
                        <h3 class="font-semibold">新增筆記</h3>
                        <USwitch v-model="createNoteState.star" />
                    </div>
                    <UFormField label="title" name="title" required>
                        <UInput
                            v-model="createNoteState.title"
                            type="text"
                            class="w-full" />
                    </UFormField>
                    <UFormField label="keypoint" name="keypoint">
                        <UInput
                            v-model="createNoteState.keypoint"
                            type="text"
                            class="w-full" />
                    </UFormField>

                    <UFormField label="content" name="content">
                        <UInput
                            v-model="createNoteState.content"
                            type="textarea"
                            class="w-full" />
                    </UFormField>

                    <UButton type="submit" class="w-full">Create</UButton>
                </UForm>
            </UCard>
        </div>
    </div>
</template>
<script setup lang="ts">
import * as z from "zod";
import { computed, onMounted, ref } from "vue";
import { navigateTo, useRoute } from "#imports";
import { useToast } from "#imports";
import { useNote } from "~/composables/note/useNote";

import type { FormSubmitEvent } from "@nuxt/ui";
import type { Note } from "~/types/Note";
import type { Tag } from "~/types/Tag";

const route = useRoute();
const toast = useToast();

const noteId = ref<string>("");
const userId = ref<string>("");

const { indexNotes, createNote, updateNote, deleteNote } = useNote();
const notes = ref<Note[]>([]);

const createNoteSchema = z.object({
    title: z.string().nonempty("筆記本名稱不能為空值"),
    content: z.string(),
    keypoint: z.string(),
    star: z.boolean(),
});

type CreateNoteSchema = z.output<typeof createNoteSchema>;

const createNoteState = reactive<Partial<CreateNoteSchema>>({
    title: "",
    content: "",
    keypoint: "",
    star: false,
});

const updateNoteSechema = z.object({
    id: z.string(),
    title: z.string().nonempty("筆記本名稱不能為空值"),
    content: z.string(),
    keypoint: z.string(),
    star: z.boolean(),
});

type UpdateNoteSchema = z.output<typeof updateNoteSechema>;

const concatNotesTitle = (tags: Tag[]) => {
    return tags.map((tag) => tag.title).join(",");
};

async function getNotes() {
    const res = await indexNotes(userId.value, noteId.value);
    notes.value = res.items;
}

async function _updateNote(note: Note) {
    await updateNote(userId.value, note);
    getNotes();
}

async function _deleteNote(id: string) {
    await deleteNote(userId.value, id);
    toast.add({
        title: "Success",
        description: "The form has been submitted.",
        color: "primary",
    });

    getNotes();
}

async function onCreateNoteSubmit(event: FormSubmitEvent<CreateNoteSchema>) {
    const { title, keypoint, content, star } = event.data;
    const notebookId = route.params.id as string;
    await createNote(userId.value, {
        title,
        keypoint,
        content,
        star,
        notebookId,
    });
    toast.add({
        title: "Success",
        description: "The form has been submitted.",
        color: "primary",
    });

    await getNotes();
}

async function onUpdateNoteSubmit(event: FormSubmitEvent<UpdateNoteSchema>) {
    const { id, title, keypoint, star, content } = event.data;
    const notebookId = route.params.id as string;
    await updateNote(userId.value, {
        title,
        star,
        content,
        keypoint,
        notebookId,
        id,
    });
    toast.add({
        title: "Success",
        description: "The form has been submitted.",
        color: "primary",
    });

    await getNotes();
}

onMounted(async () => {
    const id = route.params.id as string;
    if (!id) navigateTo("/notes");
    const _userId = localStorage.getItem("userId") as string;
    if (!_userId) navigateTo("/");

    userId.value = _userId;
    noteId.value = id;

    getNotes();
});
</script>

<style></style>

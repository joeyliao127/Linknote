<template>
    <div class="p-4 space-y-4">
        <USeparator>
            <h2 class="title text-2xl">Notes</h2>
        </USeparator>

        <nuxt-link to="/notebooks">
            <UButton>To Notebook</UButton>
        </nuxt-link>

        <div class="grid grid-cols-3 gap-4">
            <template v-for="note in notes">
                <UCard>
                    <UForm
                        :state="note"
                        :schema="updateNoteSechema"
                        @submit="onUpdateNoteSubmit"
                        class="space-y-2">
                        <div class="flex flex-col">
                            <USwitch v-model="note.star" class="self-end" />
                            <UFormField label="id" name="id">
                                <UInput
                                    v-model="note.id"
                                    type="text"
                                    disabled
                                    class="w-full mt-4" />
                            </UFormField>
                        </div>

                        <UFormField label="title" name="title">
                            <UInput
                                v-model="note.title"
                                type="text"
                                class="w-full" />
                        </UFormField>
                        <UFormField label="keypoint" name="keypoint">
                            <UInput
                                v-model="note.keypoint"
                                type="text"
                                class="w-full" />
                        </UFormField>
                        <UFormField label="content" name="content">
                            <UInput
                                v-model="note.content"
                                type="text"
                                class="w-full" />
                        </UFormField>

                        <div class="flex">
                            <USelect
                                v-model="note.tagIdList"
                                :items="tagSelection"
                                multiple
                                class="w-full" />
                            <UButton
                                @click.prevent="
                                    _addTags(note.id, note.tagIdList)
                                ">
                                儲存
                            </UButton>
                        </div>

                        <div class="flex justify-between">
                            <UButton type="submit">更新</UButton>
                            <UButton @click.prevent="_deleteNote(note.id)">
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
import { useNote } from "~/composables/model/useNote";

import type { FormSubmitEvent, SelectItem } from "@nuxt/ui";
import type { Note } from "~~/types/Note";
import type { Tag } from "~~/types/Tag";
import { useTag } from "~/composables/model/useTag";
import { toSelection } from "~/composables/utils/useFormat";

const route = useRoute();
const toast = useToast();
const { indexNotes, createNote, updateNote, deleteNote, addTags } = useNote();
const { indexTags } = useTag();

const notes = ref<Note[]>([]);
const tags = ref<SelectItem[]>([]);
const notebookId = ref<string>("");
const userId = ref<string>("");

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

const concatNotesTitle = (tags: Tag[]): string => {
    return tags.map((tag) => tag.title).join(",");
};

const tagSelection = computed(() => {
    return toSelection(tags.value, "title", "id");
});

async function getNotes() {
    const res = await indexNotes(userId.value, notebookId.value);
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

async function _addTags(id: string, tagIdList: string[]) {
    await addTags(id, tagIdList);
    toast.add({
        title: "Success",
        description: "The form has been submitted.",
        color: "primary",
    });
}

onMounted(async () => {
    const id = route.params.id as string;
    tags.value = (await indexTags()).items;
    notebookId.value = id;

    getNotes();
});
</script>

<style></style>

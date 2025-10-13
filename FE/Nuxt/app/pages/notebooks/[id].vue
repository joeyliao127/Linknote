<template>
    <div class="p-4 space-y-4">
        <USeparator>
            <h2 class="title text-2xl">Notes</h2>
        </USeparator>

        <div class="grid grid-cols-4 gap-4">
            <template v-for="value in notes">
                <UCard>
                    <h3>{{ value.title }}</h3>
                    <p>{{ value.content }}</p>
                    <p>id: {{ value.id }}</p>
                    <p>tags: {{ concatTagsTitle(value.tags) }}</p>
                </UCard>
            </template>
        </div>
        <nuxt-link to="/notebooks">
            <UButton>Notebook</UButton>
        </nuxt-link>
    </div>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { navigateTo, useRoute } from "#imports";
import { useToast } from "#imports";
import type { Note } from "~/types/Note";
import { useNote } from "~/composables/note/useNote";
import type { Tag } from "~/types/Tag";

const route = useRoute();
const notebookId = ref<string>("");
const userId = ref<string>("");

const { indexNotes, createNote, updateNote, deleteNote } = useNote();
const notes = ref<Note[]>([]);

const concatTagsTitle = (tags: Tag[]) => {
    return tags.map((tag) => tag.title).join(",");
};

async function getNotes() {
    const res = await indexNotes(notebookId.value, userId.value);
    notes.value = res.items;
}

onMounted(async () => {
    const id = route.params.id as string;
    if (!id) navigateTo("/notebooks");
    const _userId = localStorage.getItem("userId") as string;
    if (!_userId) navigateTo("/");

    userId.value = _userId;
    notebookId.value = id;

    getNotes();
});
</script>

<style></style>

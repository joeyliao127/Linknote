<template>
    <NoteShell>
        <template #sidebar>
            <NoteSidebar
                :is-open="isSidebarOpen"
                :notebook-name="notebookName"
                :notes="notes"
                :current-note-id="currentNote?.id || ''"
                @close="isSidebarOpen = false"
                @note-select="handleNoteSelect"
                @return="handleReturn" />
        </template>

        <template #header>
            <div
                class="flex justify-between items-center border-y border-gray-800 px-2 h-12">
                <template v-if="currentNote">
                    <UButton
                        color="text-slate-100"
                        variant="ghost"
                        class="hover:bg-gray-800"
                        icon="i-lucide-menu"
                        @click="isSidebarOpen = true" />

                    <div
                        class="flex items-center h-8 border border-gray-900 text-slate-100 cursor-pointer hover:border-gray-500 pl-4 pr-2 rounded-md">
                        <p class="mr-2">{{ currentNote.title }}</p>
                        <UButton
                            color="text-slate-100"
                            variant="ghost"
                            icon="i-lucide-pen" />
                    </div>

                    <div class="flex items-center gap-x-2">
                        <UButton
                            icon="i-lucide-tag"
                            class="hover:bg-gray-800"
                            color="slate-100" />
                        <StarButton
                            :id="currentNote.id"
                            :stared="currentNote.star"
                            size="lg"
                            @change="toggleStar" />

                        <UButton
                            icon="i-lucide-trash"
                            class="hover:bg-gray-800"
                            color="slate-100" />
                    </div>
                </template>
            </div>
        </template>

        <template #main>
            <p>main</p>
        </template>
    </NoteShell>
</template>

<script setup lang="ts">
definePageMeta({ layout: "main" });

import { ref, onMounted } from "vue";
import { NoteShell } from "#components";
import StarButton from "~/components/ui/StarButton.vue";
import NoteSidebar from "~/components/note/NoteSidebar.vue"; // 根據你的目錄結構調整
import type { Note } from "~~/types/Note";
import { useNote } from "~/composables/model/useNote";
import { useNotebook } from "~/composables/model/useNotebook";

const route = useRoute();
const router = useRouter();
const { indexNotes, getNote, updateNote, deleteNote } = useNote();
const { getNotebook } = useNotebook();

const currentNote = ref<Note>();
const notes = ref<Note[]>([]);
const notebookName = ref("我的筆記本"); // 從 API 或 route 取得
const isSidebarOpen = ref(false);

async function toggleStar(value: { id: string; stared: boolean }) {
    console.log("change");
    console.log(value.id);
    console.log(value.stared);

    if (currentNote.value) {
        currentNote.value.star = value.stared;

        // 更新到後端
        // await updateNote(currentNote.value.userId, {
        //     ...currentNote.value,
        //     star: value.stared,
        // });

        // 同步更新 notes 列表
        // const noteInList = notes.value.find((n) => n.id === value.id);
        // if (noteInList) {
        //     noteInList.star = value.stared;
        // }
    }
}

async function handleNoteSelect(noteId: string) {
    // 導航到選中的筆記
    await router.push(`/notes/${noteId}`);
}

function handleReturn() {
    // 返回筆記本列表
    const notebookId = route.query.notebookId || currentNote.value?.notebookId;
    if (notebookId) {
        router.push(`/notebooks/${notebookId}/notes`);
    } else {
        router.push("/notebooks");
    }
}

async function fetchNotes() {
    // 取得同一個筆記本的所有筆記
    if (currentNote.value?.notebookId) {
        const result = await indexNotes(currentNote.value.notebookId);
        const notebook = await getNotebook(currentNote.value.notebookId);
        notes.value = result.items || [];
    }
}

onMounted(async () => {
    const note = await getNote(route.params.id as string);
    // const notes =
    console.log(note);

    currentNote.value = note;

    // 取得筆記本名稱和其他筆記
    await fetchNotes();
});
</script>

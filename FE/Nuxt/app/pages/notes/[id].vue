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
                class="flex justify-between items-center border-b border-gray-500 px-2 h-12">
                <template v-if="currentNote">
                    <UButton
                        color="gray"
                        variant="ghost"
                        class="hover:bg-gray-800"
                        icon="i-lucide-menu"
                        @click="isSidebarOpen = true" />

                    <FormTitleEditor
                        v-model="currentNote.title"
                        @change="saveNote" />

                    <div class="flex items-center gap-x-2">
                        <UTooltip text="Create new note">
                            <UButton
                                icon="i-lucide-plus"
                                class="hover:bg-gray-800"
                                color="gray"
                                variant="ghost" />
                        </UTooltip>

                        <UTooltip text="toggle star">
                            <StarButton
                                :id="currentNote.id"
                                :stared="currentNote.star"
                                @change="toggleStar" />
                        </UTooltip>

                        <UTooltip text="delete note">
                            <UButton
                                icon="i-lucide-trash"
                                class="hover:bg-gray-800"
                                color="gray"
                                variant="ghost"
                                @click="handleDelete" />
                        </UTooltip>
                    </div>
                </template>
            </div>
        </template>

        <template #main>
            <div v-if="currentNote" class="h-full flex bg-gray-900">
                <!-- Left Panel - Note Content -->
                <ResizablePanel
                    orientation="vertical"
                    :default-size="leftPanelWidth"
                    :min-size="400"
                    :max-size="1200"
                    storage-key="note-left-panel-width">
                    <NoteEditor
                        v-model="currentNote.content"
                        title="筆記內容"
                        placeholder="在這裡開始撰寫筆記..."
                        :default-height="600"
                        :min-height="300"
                        :max-height="1000"
                        storage-key="note-content-height"
                        @change="handleContentChange" />
                </ResizablePanel>

                <!-- Right Panel - Keypoint, Question, Tags -->
                <div class="flex-1 flex flex-col overflow-hidden">
                    <!-- Question Editor -->
                    <NoteEditor
                        v-model="currentNote.question"
                        title="問題"
                        subtitle="這篇筆記想解決什麼問題或目的是什麼？"
                        placeholder="記錄這篇筆記要解決的問題..."
                        :default-height="questionHeight"
                        :min-height="150"
                        :max-height="600"
                        storage-key="note-question-height"
                        @change="handleQuestionChange" />

                    <!-- Keypoint Editor -->
                    <NoteEditor
                        v-model="currentNote.keypoint"
                        title="重點整理"
                        subtitle="此區塊可簡單記錄概要"
                        placeholder="記錄重點摘要..."
                        :default-height="keypointHeight"
                        :min-height="150"
                        :max-height="600"
                        storage-key="note-keypoint-height"
                        @change="handleKeypointChange" />

                    <!-- Tags Section -->
                    <div class="border border-slate-700 bg-slate-900 p-4 h-40">
                        <h3 class="text-sm font-semibold text-slate-100 mb-3">
                            標籤
                        </h3>
                        <div class="h-[100px] overflow-y-auto">
                            <UInputTags
                                v-model="noteTags"
                                placeholder="新增標籤..."
                                @addTag="handleTagAdd"
                                @removeTag="handleTagRemove" />
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </NoteShell>
</template>

<script setup lang="ts">
definePageMeta({ layout: "main" });

import { ref, computed, onMounted } from "vue";
import { NoteShell } from "#components";
import StarButton from "~/components/ui/StarButton.vue";
import NoteSidebar from "~/components/note/NoteSidebar.vue";
import ResizablePanel from "~/components/ui/ResizablePanel.vue";
import NoteEditor from "~/components/note/NoteEditor.vue";
import type { Note } from "~~/types/Note";
import { useNote } from "~/composables/model/useNote";
import { useNotebook } from "~/composables/model/useNotebook";
import { useTag } from "~/composables/model/useTag";
import FormTitleEditor from "~/components/form/FormTitleEditor.vue";
import type { Tag } from "~~/types/Tag";
import { user } from "#build/ui";

const route = useRoute();
const router = useRouter();
const dialog = useDialogs();

const { indexNotes, getNote, updateNote, deleteNote, addTags } = useNote();
const { getNotebook } = useNotebook();
const { indexTags, createTag } = useTag();

const currentNote = ref<Note>();
const notes = ref<Note[]>([]);
const notebookName = ref("我的筆記本");
const isSidebarOpen = ref(false);
const isModify = ref(false);

// Panel sizes
const leftPanelWidth = ref(800);
const questionHeight = computed(() => {
    // Calculate based on available space minus tags height (160px) and gaps
    return 300;
});
const keypointHeight = computed(() => {
    return 300;
});

interface tagItem {
    id: string;
    title: string;
}

let userTags: tagItem[] = [];
const noteTags = ref<string[]>([]);

async function toggleStar(value: { id: string; stared: boolean }) {
    if (currentNote.value) {
        currentNote.value.star = value.stared;
        await saveNote();

        // Sync with notes list
        const noteInList = notes.value.find((n) => n.id === value.id);
        if (noteInList) {
            noteInList.star = value.stared;
        }
    }
}

async function handleNoteSelect(noteId: string) {
    await router.push(`/notes/${noteId}`);
}

function handleReturn() {
    const notebookId = route.query.notebookId || currentNote.value?.notebookId;
    if (notebookId) {
        router.push(`/notebooks/${notebookId}/notes`);
    } else {
        router.push("/notebooks");
    }
}

async function handleDelete() {
    dialog.confirm(
        "確定要刪除這篇筆記嗎？此操作無法復原。",
        "刪除筆記",
        async () => {
            if (currentNote.value) {
                await deleteNote(currentNote.value.id);
                dialog.inform("筆記已成功刪除");
                handleReturn();
            }
        }
    );
}

// Save handlers - you can implement debounce or manual save here
async function handleContentChange(value: string) {
    // TODO: Implement auto-save or manual save
    await saveNote();
    console.log("Content changed:", value);
}

async function handleQuestionChange(value: string) {
    await saveNote();
    // TODO: Implement auto-save or manual save
    console.log("Question changed:", value);
}

async function handleKeypointChange(value: string) {
    await saveNote();
    // TODO: Implement auto-save or manual save
    console.log("Keypoint changed:", value);
}

function handleTagsChange(tags: string[]) {
    // TODO: Implement auto-save or manual save
    console.log("Tags changed:", tags);
}

async function saveNote() {
    if (currentNote.value) {
        await updateNote(currentNote.value);
    }
}

async function fetchNotes() {
    if (currentNote.value?.notebookId) {
        const result = await indexNotes(currentNote.value.notebookId);
        const notebook = await getNotebook(currentNote.value.notebookId);
        notes.value = result.items || [];
        notebookName.value = notebook.title || "我的筆記本";
    }
}

async function fetchUserTags() {
    const res = await indexTags();
    userTags = res.items.map((it) => {
        return { id: it.id, title: it.title };
    });
    console.log("init");
    console.log(userTags);
}

async function handleTagAdd(val: string) {
    await nextTick();
    if (isTagNotExist(val, userTags)) {
        const tag = await createTag(val);
        userTags.push({ id: tag.id, title: tag.title });
        noteTags.value.push(tag.title);
    }

    await updateNoteTags();

    function isTagNotExist(target: string, tags: tagItem[]) {
        for (let it of tags) {
            if (it.title === target) {
                return false;
            }
        }
        return true;
    }
}

async function handleTagRemove() {
    // nextTick 等待畫面變更後 noteTags value 才會更新
    await nextTick();
    await updateNoteTags();
}

async function updateNoteTags() {
    const tagIdList = userTags
        .filter((it) => {
            return noteTags.value.includes(it.title);
        })
        .map((it) => it.id);

    await addTags(currentNote.value?.id, tagIdList);
}

onMounted(async () => {
    const note = await getNote(route.params.id as string);
    noteTags.value = note.tags.map((it) => it.title);
    currentNote.value = note;
    await fetchNotes();
    await fetchUserTags();
});
</script>

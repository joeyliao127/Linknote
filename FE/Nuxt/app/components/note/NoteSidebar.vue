<template>
    <div>
        <!-- Overlay backdrop -->
        <div
            :class="[
                'fixed inset-0 bg-black/50 z-40 transition-opacity',
                isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none',
            ]"
            @click="$emit('close')" />

        <!-- Sidebar -->
        <aside
            :class="[
                'fixed left-0 top-0 h-screen w-72 bg-slate-900 border-r border-slate-800 z-50 transition-transform duration-300',
                isOpen ? 'translate-x-0' : '-translate-x-full',
            ]">
            <div class="flex items-start flex-col h-full">
                <div class="pl-2 py-2">
                    <UButton
                        variant="ghost"
                        color="slate-800"
                        icon="i-lucide-panel-left-close"
                        size="xl"
                        @click="emit('close')" />
                </div>

                <!-- Top section - Return button -->
                <div class="w-full px-4 py-4 border-y border-slate-800">
                    <UButton
                        color="accent"
                        class="w-full justify-start"
                        @click="handleReturn">
                        <template #leading>
                            <UIcon
                                name="i-lucide-chevron-left"
                                class="w-4 h-4" />
                        </template>
                        返回筆記本列表
                    </UButton>
                </div>

                <!-- Middle section - Notebook and notes -->
                <UScrollArea class="flex-1 p-4">
                    <div class="mb-4">
                        <div class="flex items-center justify-between mb-3">
                            <h3 class="font-semibold text-slate-100">
                                {{ notebookName }}
                            </h3>
                        </div>

                        <div class="space-y-1">
                            <button
                                v-for="note in filteredNotes"
                                :key="note.id"
                                :class="[
                                    'w-full text-left px-3 py-2 rounded-md text-sm transition-colors',
                                    'hover:bg-slate-800',
                                    currentNoteId === note.id
                                        ? 'bg-slate-800 text-slate-100'
                                        : 'text-slate-300',
                                ]"
                                @click="handleNoteSelect(note.id)">
                                <div class="flex items-center justify-between">
                                    <span class="truncate">
                                        {{ note.title }}
                                    </span>
                                    <UIcon
                                        v-if="note.star"
                                        name="i-lucide-star"
                                        class="h-3 w-3 fill-yellow-500 text-yellow-500 flex-shrink-0 ml-2" />
                                </div>
                            </button>
                        </div>
                    </div>
                </UScrollArea>

                <!-- Bottom section - Settings -->
                <div class="w-full p-4 border-t border-slate-800">
                    <SettingsModal />
                </div>
            </div>
        </aside>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import SettingsModal from "../dashboard/SettingsModal.vue";

interface Note {
    id: string;
    title: string;
    star: boolean;
}

interface Props {
    isOpen: boolean;
    notebookName: string;
    notes: Note[];
    currentNoteId: string;
}

const props = defineProps<Props>();

const emit = defineEmits<{
    close: [];
    noteSelect: [noteId: string];
    return: [];
}>();

const filterStarred = ref(false);
const settingsOpen = ref(false);

const filteredNotes = computed(() => {
    return props.filterStarred
        ? props.notes.filter((note) => note.star)
        : props.notes;
});

const handleNoteSelect = (noteId: string) => {
    emit("noteSelect", noteId);
    emit("close");
};

const handleReturn = () => {
    emit("return");
};
</script>

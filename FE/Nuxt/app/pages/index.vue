<template>
    <div class="p-6 space-y-6">
        <!-- Header -->
        <div class="flex items-center justify-between">
            <div>
                <p class="text-sm text-slate-400">{{ $t('pages.index.breadcrumb') }}</p>
                <h1 class="text-2xl font-semibold text-white">
                    {{ $t('pages.index.greeting') }}
                </h1>
            </div>
            <UButton icon="i-lucide-plus" color="accent" variant="soft">
                {{ $t('pages.index.addNote') }}
            </UButton>
        </div>

        <!-- Stats Grid -->
        <div class="grid gap-4 md:grid-cols-3">
            <template v-for="stat in overview" :key="stat.label">
                <UCard class="bg-slate-900/60 border-slate-800">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm text-slate-400">
                                {{ stat.label }}
                            </p>
                            <p class="text-3xl font-bold mt-1">
                                {{ stat.value }}
                            </p>
                        </div>
                        <div
                            class="h-10 w-10 rounded-xl bg-accent/20 text-accent flex items-center justify-center">
                            <UIcon :name="stat.icon" class="w-5 h-5" />
                        </div>
                    </div>
                </UCard>
            </template>
        </div>

        <!-- Recent Notes -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <div class="flex items-center justify-between mb-4">
                <div>
                    <p class="text-lg font-semibold">{{ $t('pages.index.recentNotes') }}</p>
                    <p class="text-sm text-slate-400">{{ $t('pages.index.recentNotesDesc') }}</p>
                </div>
                <UButton variant="ghost" icon="i-lucide-arrow-right">
                    {{ $t('pages.index.viewAll') }}
                </UButton>
            </div>
            <div class="grid gap-3 md:grid-cols-3">
                <template v-for="note in recentNotes" :key="note.id">
                    <NoteCard
                        :id="note.id"
                        :title="note.title"
                        :tags="note.tags || []"
                        :updated-at="note.updatedAt"
                        @open="goNote(note.id)" />
                </template>
            </div>
        </UCard>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "#imports";
import NoteCard from "~/components/ui/NoteCard.vue";
import { useNotebook } from "~/composables/model/useNotebook";
import { useNote } from "~/composables/model/useNote";
import type { Note } from "~~/types/Note";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const { t } = useI18n();
const { indexNotebook } = useNotebook();
const { listNotes } = useNote();

const notebookCount = ref(0);
const noteCount = ref(0);
const collabCount = ref(0);
const recentNotes = ref<Note[]>([]);

const overview = computed(() => [
    { label: t('pages.index.statNotebook'), value: notebookCount.value, icon: "i-lucide-notebook-pen" },
    { label: t('pages.index.statNote'), value: noteCount.value, icon: "i-lucide-sticky-note" },
    { label: t('pages.index.statCollab'), value: collabCount.value, icon: "i-lucide-users" },
]);

onMounted(async () => {
    const [notebooksRes, notesRes, collabRes, recentRes] = await Promise.all([
        indexNotebook({ pageSize: 1 }),
        listNotes({ pageSize: 1 }),
        indexNotebook({ collab: true, pageSize: 1 }),
        listNotes({
            pageSize: 6,
            orderBy: "updated_at",
            orderDirection: "desc",
        }),
    ]);
    notebookCount.value = notebooksRes.count;
    noteCount.value = notesRes.count;
    collabCount.value = collabRes.count;
    recentNotes.value = recentRes.items;
});

function goNote(id: string) {
    router.push(`/notes/${id}`);
}
</script>

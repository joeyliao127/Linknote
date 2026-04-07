<template>
    <div class="p-6 space-y-6">
        <!-- Header -->
        <div class="flex items-center justify-between">
            <div>
                <p class="text-sm text-slate-400">{{ $t('pages.rag.breadcrumb') }}</p>
                <h1 class="text-2xl font-semibold text-white">{{ $t('pages.rag.title') }}</h1>
            </div>
        </div>

        <!-- 筆記選擇區 -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <template #header>
                <div class="flex items-center justify-between">
                    <p class="font-semibold">{{ $t('pages.rag.addRag') }}</p>
                    <div class="flex items-center gap-3">
                        <USelect
                            v-model="selectedNotebookId"
                            :items="notebookOptions"
                            :placeholder="$t('pages.rag.notebookPlaceholder')"
                            class="w-64"
                            @update:model-value="onNotebookChange" />
                        <UButton
                            :disabled="selectedNoteIds.size === 0 || ingesting"
                            :loading="ingesting"
                            color="primary"
                            class="text-white"
                            icon="i-lucide-brain"
                            @click="handleIngest">
                            {{ $t('pages.rag.addRagBtn', { count: selectedNoteIds.size }) }}
                        </UButton>
                    </div>
                </div>
            </template>

            <UTable
                v-if="selectedNotebookId"
                :data="notes"
                :columns="noteColumns"
                :loading="notesLoading"
                class="w-full">
                <template #select-cell="{ row }">
                    <UCheckbox
                        :model-value="selectedNoteIds.has(row.original.id)"
                        :disabled="isNoteDisabled(row.original.id)"
                        @update:model-value="toggleNote(row.original.id)" />
                </template>
                <template #title-cell="{ row }">
                    <span
                        class="text-sm"
                        :class="
                            isNoteDisabled(row.original.id)
                                ? 'text-slate-500'
                                : ''
                        ">
                        {{ row.original.title || $t('pages.notebookNotes.untitledNote') }}
                    </span>
                </template>
                <template #updatedAt-cell="{ row }">
                    <span class="text-xs text-slate-400">
                        {{ formatDate(row.original.updatedAt) }}
                    </span>
                </template>
            </UTable>
            <p v-else class="text-sm text-slate-500 py-6 text-center">
                {{ $t('pages.rag.selectNotebookFirst') }}
            </p>
        </UCard>

        <!-- 狀態總覽表格 -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <template #header>
                <div class="flex items-center justify-between">
                    <p class="font-semibold">{{ $t('pages.rag.ragStatus') }}</p>
                    <UButton
                        v-if="selectedNotebookId"
                        variant="ghost"
                        icon="i-lucide-refresh-cw"
                        size="sm"
                        :loading="statusLoading"
                        @click="loadRagNotes" />
                </div>
            </template>

            <UTable
                :data="ragNotes"
                :columns="ragColumns"
                :loading="statusLoading"
                class="w-full">
                <template #noteTitle-cell="{ row }">
                    <span
                        :class="
                            row.original.status === 'deleted'
                                ? 'line-through text-slate-500'
                                : ''
                        ">
                        {{ row.original.noteTitle ?? $t('pages.rag.deleted') }}
                    </span>
                </template>
                <template #noteUpdatedAt-cell="{ row }">
                    {{
                        row.original.noteUpdatedAt
                            ? formatDate(row.original.noteUpdatedAt)
                            : "—"
                    }}
                </template>
                <template #ragUpdatedAt-cell="{ row }">
                    {{
                        row.original.ragUpdatedAt
                            ? formatDate(row.original.ragUpdatedAt)
                            : "—"
                    }}
                </template>
                <template #status-cell="{ row }">
                    <UBadge
                        :color="statusColor(row.original.status)"
                        variant="subtle">
                        {{
                            statusLabel(
                                row.original.status,
                                row.original.behindDays,
                            )
                        }}
                    </UBadge>
                </template>
                <template #action-cell="{ row }">
                    <UButton
                        icon="i-lucide-trash-2"
                        color="red"
                        variant="ghost"
                        size="xs"
                        :loading="deletingNoteId === row.original.noteId"
                        @click="openConfirm(row.original)" />
                </template>
            </UTable>
        </UCard>

        <!-- 刪除確認 Dialog -->
        <UModal v-model:open="confirmOpen" :dismissible="false">
            <template #header>
                <p class="font-semibold">{{ $t('pages.rag.removeConfirmTitle') }}</p>
            </template>
            <template #body>
                <p class="text-sm text-slate-300">
                    {{ $t('pages.rag.removeConfirm', { title: confirmTarget?.noteTitle ?? $t('pages.rag.thisNote') }) }}
                </p>
            </template>
            <template #footer>
                <div class="flex justify-end gap-2">
                    <UButton
                        variant="ghost"
                        color="neutral"
                        @click="confirmOpen = false">
                        {{ $t('common.cancel') }}
                    </UButton>
                    <UButton
                        color="red"
                        :loading="deletingNoteId !== null"
                        @click="confirmDelete">
                        {{ $t('pages.rag.removeConfirmBtn') }}
                    </UButton>
                </div>
            </template>
        </UModal>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, useToast } from "#imports";
import { useNotebook } from "~/composables/model/useNotebook";
import { useRag } from "~/composables/model/useRag";
import type { RagNote } from "~~/types/RagNote";

definePageMeta({ layout: "dashboard" });

const { t } = useI18n();
const toast = useToast();
const { indexNotebook } = useNotebook();
const { ingest, getRagNotes } = useRag();

const notebooks = ref<{ id: string; title: string }[]>([]);
const selectedNotebookId = ref<string>("");
const notes = ref<any[]>([]);
const notesLoading = ref(false);
const selectedNoteIds = ref(new Set<string>());
const ingesting = ref(false);
const ragNotes = ref<RagNote[]>([]);
const statusLoading = ref(false);
const deletingNoteId = ref<string | null>(null);
const confirmOpen = ref(false);
const confirmTarget = ref<RagNote | null>(null);

const notebookOptions = computed(() =>
    notebooks.value.map((nb) => ({ label: nb.title, value: nb.id })),
);

const ragStatusMap = computed(
    () => new Map(ragNotes.value.map((r) => [r.noteId, r.status])),
);

function isNoteDisabled(noteId: string) {
    return ragStatusMap.value.get(noteId) === "up_to_date";
}

const noteColumns = computed(() => [
    { id: "select", header: "" },
    { accessorKey: "title", header: t('pages.rag.colNote') },
    { accessorKey: "updatedAt", header: t('pages.rag.colDate') },
]);

const ragColumns = computed(() => [
    { accessorKey: "noteTitle", header: t('pages.rag.colNote') },
    { accessorKey: "noteUpdatedAt", header: t('pages.rag.colNoteUpdated') },
    { accessorKey: "ragUpdatedAt", header: t('pages.rag.colRagUpdated') },
    { accessorKey: "status", header: t('pages.rag.colStatus') },
    { id: "action", header: "" },
]);

async function loadNotebooks() {
    const res = await indexNotebook({ page: 1, pageSize: 50 });
    notebooks.value = res.items ?? [];
}

async function onNotebookChange(notebookId: string) {
    selectedNoteIds.value.clear();
    notes.value = [];
    notesLoading.value = true;
    try {
        const res = await $fetch<{ items: any[] }>(
            `/api/notebooks/${notebookId}/notes`,
            { query: { pageSize: 100 } },
        );
        notes.value = res.items ?? [];
    } finally {
        notesLoading.value = false;
    }
    await loadRagNotes();
}

async function loadRagNotes() {
    statusLoading.value = true;
    try {
        ragNotes.value = await getRagNotes();
    } finally {
        statusLoading.value = false;
    }
}

function toggleNote(noteId: string) {
    if (selectedNoteIds.value.has(noteId)) {
        selectedNoteIds.value.delete(noteId);
    } else {
        selectedNoteIds.value.add(noteId);
    }
}

async function handleIngest() {
    if (selectedNoteIds.value.size === 0) return;
    ingesting.value = true;
    try {
        const count = selectedNoteIds.value.size;
        await ingest([...selectedNoteIds.value]);
        selectedNoteIds.value.clear();
        await loadRagNotes();
        toast.add({
            title: t('pages.rag.ingestSuccess'),
            description: t('pages.rag.ingestSuccessDesc', { count }),
            color: "success",
        });
    } catch {
        toast.add({
            title: t('pages.rag.ingestFailed'),
            description: t('common.retryLater'),
            color: "error",
        });
    } finally {
        ingesting.value = false;
    }
}

function openConfirm(ragNote: RagNote) {
    confirmTarget.value = ragNote;
    confirmOpen.value = true;
}

async function confirmDelete() {
    if (!confirmTarget.value) return;
    const noteId = confirmTarget.value.noteId;
    confirmOpen.value = false;
    deletingNoteId.value = noteId;
    try {
        await $fetch(`/api/rag/notes/${noteId}`, { method: "DELETE" });
        await loadRagNotes();
        toast.add({ title: t('pages.rag.removeSuccess'), color: "success" });
    } catch {
        toast.add({ title: t('pages.rag.removeFailed'), color: "error" });
    } finally {
        deletingNoteId.value = null;
        confirmTarget.value = null;
    }
}

function formatDate(dateStr: string) {
    return new Date(dateStr).toLocaleDateString("zh-TW", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
    });
}

function statusColor(status: string) {
    return status === "up_to_date"
        ? "green"
        : status === "outdated"
          ? "yellow"
          : "red";
}

function statusLabel(status: string, behindDays: number) {
    if (status === "up_to_date") return t('pages.rag.statusUpToDate');
    if (status === "outdated") return t('pages.rag.statusOutdated', { days: behindDays });
    return t('pages.rag.statusDeleted');
}

onMounted(() => {
    loadNotebooks();
    loadRagNotes();
});
</script>

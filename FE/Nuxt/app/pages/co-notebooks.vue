<template>
    <div class="flex flex-col h-full gap-6 p-6">
        <!-- Header -->
        <div>
            <p class="text-sm text-slate-200/50">{{ $t('pages.coNotebooks.breadcrumb') }}</p>
            <h1 class="text-3xl font-semibold text-slate-50/95 leading-tight">{{ $t('pages.coNotebooks.title') }}</h1>
        </div>

        <!-- Content -->
        <div class="flex-1">
            <div v-if="loading" class="flex items-center justify-center py-20">
                <UIcon name="i-lucide-loader-2" class="w-6 h-6 animate-spin text-slate-400" />
            </div>

            <div
                v-else-if="notebooks.length > 0"
                class="grid grid-cols-[repeat(auto-fill,minmax(320px,1fr))] gap-4">
                <NotebookCard
                    v-for="nb in notebooks"
                    :key="nb.id"
                    :id="nb.id"
                    :title="nb.title"
                    :description="nb.description"
                    :deletable="false"
                    :collab="true"
                    @click="goNotebook(nb.id)" />
            </div>

            <div v-else class="flex flex-col items-center justify-center py-20 gap-3 text-slate-500">
                <UIcon name="i-lucide-book-open" class="w-10 h-10" />
                <p class="text-sm">{{ $t('pages.coNotebooks.empty') }}</p>
                <p class="text-xs">{{ $t('pages.coNotebooks.emptyHint') }}</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, navigateTo } from "#imports";
import { useNotebook } from "~/composables/model/useNotebook";
import NotebookCard from "~/components/ui/NotebookCard.vue";
import type { Notebook } from "~~/types/Notebook";

definePageMeta({ layout: "dashboard" });

const { indexNotebook } = useNotebook();

const notebooks = ref<Notebook[]>([]);
const loading = ref(false);

async function load() {
    loading.value = true;
    try {
        const res = await indexNotebook({ collab: true, pageSize: 50 });
        notebooks.value = res.items ?? [];
    } finally {
        loading.value = false;
    }
}

function goNotebook(id: string) {
    navigateTo(`/notebooks/${id}/notes`);
}

onMounted(load);
</script>

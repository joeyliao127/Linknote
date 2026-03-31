<template>
    <div class="p-6 space-y-6">
        <!-- Header -->
        <div class="flex items-center justify-between">
            <div>
                <p class="text-sm text-slate-400">概覽</p>
                <h1 class="text-2xl font-semibold text-white">早安，歡迎回來</h1>
            </div>
            <UButton icon="i-lucide-plus" color="accent" variant="soft">
                新增筆記
            </UButton>
        </div>

        <!-- Stats Grid -->
        <div class="grid gap-4 md:grid-cols-3">
            <template v-for="stat in overview" :key="stat.label">
                <UCard class="bg-slate-900/60 border-slate-800">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-sm text-slate-400">{{ stat.label }}</p>
                            <p class="text-3xl font-bold mt-1">{{ stat.value }}</p>
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
                    <p class="text-lg font-semibold">最近開啟的筆記</p>
                    <p class="text-sm text-slate-400">點擊可快速前往筆記內容</p>
                </div>
                <UButton variant="ghost" icon="i-lucide-arrow-right">
                    查看全部
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
import { useRouter } from "#imports";
import NoteCard from "~/components/ui/NoteCard.vue";
import { useDashboardMock } from "~/composables/model/useDashboardMock";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const { overview, recentNotes } = useDashboardMock();

function goNote(id: string) {
    router.push(`/notes/${id}`);
}
</script>

<template>
    <div class="w-44 py-1">
        <!-- All notes -->
        <button
            :class="[
                'w-full flex items-center gap-2.5 px-3 py-2 text-sm transition-colors rounded',
                !starOnly ? 'text-accent bg-accent/10' : 'text-slate-300 hover:bg-white/[0.06]',
            ]"
            @click="emit('update:starOnly', false)">
            <UIcon name="i-lucide-file-text" class="w-4 h-4 shrink-0" />
            All notes
        </button>

        <!-- Star only -->
        <button
            :class="[
                'w-full flex items-center gap-2.5 px-3 py-2 text-sm transition-colors rounded',
                starOnly ? 'text-accent bg-accent/10' : 'text-slate-300 hover:bg-white/[0.06]',
            ]"
            @click="emit('update:starOnly', true)">
            <UIcon
                name="i-lucide-star"
                class="w-4 h-4 shrink-0 fill-yellow-400 text-yellow-400" />
            Star
        </button>

        <div class="border-t border-slate-700/50 my-1" />

        <!-- Sort by time -->
        <button
            class="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-slate-300 hover:bg-white/[0.06] transition-colors rounded"
            @click="emit('update:sortOrder', sortOrder === 'desc' ? 'asc' : 'desc')">
            <UIcon name="i-lucide-clock" class="w-4 h-4 shrink-0" />
            <span>Sort By Time</span>
            <UIcon
                :name="sortOrder === 'desc' ? 'i-lucide-arrow-down' : 'i-lucide-arrow-up'"
                class="w-3.5 h-3.5 ml-auto text-slate-500" />
        </button>
    </div>
</template>

<script setup lang="ts">
defineProps<{
    starOnly: boolean;
    sortOrder: "asc" | "desc";
}>();

const emit = defineEmits<{
    "update:starOnly": [value: boolean];
    "update:sortOrder": [value: "asc" | "desc"];
}>();
</script>

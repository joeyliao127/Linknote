<template>
    <div class="py-2">
        <p
            v-if="headings.length === 0"
            class="px-3 text-xs text-slate-300 italic">
            No headings
        </p>
        <button
            v-for="(heading, i) in headings"
            :key="i"
            :style="{ paddingLeft: `${8 + (heading.level - 1) * 12}px` }"
            :class="[
                'w-full text-left pr-2 py-1 rounded text-slate-500 hover:text-slate-200 hover:bg-white/[0.06] transition-colors truncate',
                heading.level === 1
                    ? 'text-xs font-semibold text-slate-400'
                    : 'text-xs',
            ]"
            @click="scrollTo(heading)">
            {{ heading.text }}
        </button>
    </div>
</template>

<script setup lang="ts">
import type { HeadingItem } from "./TiptapEditor.vue";

const props = defineProps<{
    headings: HeadingItem[];
    editorEl?: HTMLElement | null;
}>();

function scrollTo(heading: HeadingItem) {
    if (!props.editorEl) return;
    const el = props.editorEl.querySelector(`#${CSS.escape(heading.id)}`);
    el?.scrollIntoView({ behavior: "smooth", block: "start" });
}
</script>

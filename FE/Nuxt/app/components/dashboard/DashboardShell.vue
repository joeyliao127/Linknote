<template>
    <div
        :class="[
            'bg-slate-950 text-slate-100',
            fullHeight ? 'min-h-screen' : 'min-h-[640px]',
        ]">
        <div class="flex min-h-inherit">
            <aside
                class="hidden lg:flex w-72 shrink-0 border-r border-slate-800 bg-slate-900/70 backdrop-blur">
                <slot name="sidebar" />
            </aside>

            <div class="flex-1 flex flex-col min-w-0">
                <header
                    v-if="hasHeader"
                    class="sticky top-0 z-10 border-b border-slate-800 bg-slate-950/80 backdrop-blur px-4 lg:px-8 py-4">
                    <slot name="header" />
                </header>

                <main class="flex-1 overflow-y-auto px-4 lg:px-8 py-6">
                    <slot />
                </main>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, useSlots } from "vue";

const props = withDefaults(
    defineProps<{
        /**
         * 是否撐滿整個螢幕高度，示範頁可關閉以便嵌入。
         */
        fullHeight?: boolean;
    }>(),
    {
        fullHeight: true,
    }
);

const slots = useSlots();
const hasHeader = computed(() => Boolean(slots.header));

const fullHeight = computed(() => props.fullHeight);
</script>

<style scoped>
.min-h-inherit {
    min-height: inherit;
}
</style>

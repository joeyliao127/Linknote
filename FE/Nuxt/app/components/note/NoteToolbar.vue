<template>
    <div
        class="flex flex-wrap items-center gap-3 rounded-2xl border border-slate-800 bg-slate-900/60 px-4 py-3">
        <UTooltip text="建立未命名筆記">
            <UButton
                color="accent"
                icon="i-lucide-plus"
                :loading="creating"
                @click="$emit('create')">
                Create
            </UButton>
        </UTooltip>

        <UTooltip text="清除所有篩選">
            <UButton variant="ghost" icon="i-lucide-wand-2" @click="$emit('reset')">
                All
            </UButton>
        </UTooltip>

        <UTooltip text="僅顯示收藏">
            <UButton
                :variant="starOnly ? 'solid' : 'ghost'"
                :color="starOnly ? 'yellow' : 'neutral'"
                icon="i-lucide-star"
                @click="$emit('toggle-star', !starOnly)">
                Star
            </UButton>
        </UTooltip>

        <USelect
            v-model="internalTag"
            :items="tagItems"
            placeholder="篩選標籤"
            class="w-44"
            @update:model-value="$emit('change-tag', internalTag || null)" />

        <UTooltip text="切換排序（更新時間）">
            <UButton
                variant="ghost"
                :color="sortOrder === 'asc' ? 'neutral' : 'accent'"
                icon="i-lucide-arrow-up-down"
                @click="$emit('change-sort', sortOrder === 'asc' ? 'desc' : 'asc')">
                {{ sortOrder === "asc" ? "升序" : "降序" }}
            </UButton>
        </UTooltip>

        <div class="flex-1 min-w-[200px]">
            <UInput
                v-model="keywordModel"
                icon="i-lucide-search"
                placeholder="關鍵字（前綴）"
                @keyup.enter="$emit('search', keywordModel)" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { Tag } from "~~/types/Tag";

const props = withDefaults(
    defineProps<{
        tags?: Tag[];
        selectedTag?: string | null;
        keyword?: string;
        starOnly?: boolean;
        sortOrder?: "asc" | "desc";
        creating?: boolean;
    }>(),
    {
        tags: () => [],
        selectedTag: null,
        keyword: "",
        starOnly: false,
        sortOrder: "desc",
        creating: false,
    }
);

const emit = defineEmits<{
    (e: "create"): void;
    (e: "reset"): void;
    (e: "toggle-star", value: boolean): void;
    (e: "change-tag", value: string | null): void;
    (e: "change-sort", value: "asc" | "desc"): void;
    (e: "search", value: string): void;
}>();

const tagItems = computed(() =>
    props.tags.map((tag) => ({
        label: tag.title,
        value: tag.id,
    }))
);

const keywordModel = computed({
    get: () => props.keyword,
    set: (value: string) => emit("search", value),
});

const internalTag = computed({
    get: () => props.selectedTag,
    set: (value: string | null) => emit("change-tag", value),
});
</script>

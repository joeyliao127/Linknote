<template>
    <div
        class="inline-flex flex-wrap items-center gap-x-3 rounded-2xl border border-slate-800 bg-slate-900/60 px-4 py-3 w-auto">
        <UTooltip text="清除所有篩選">
            <UButton
                variant="ghost"
                icon="i-lucide-wand-2"
                @click="$emit('reset')">
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

        <UTooltip text="切換排序（更新時間）">
            <UButton
                variant="ghost"
                :color="sortOrder === 'asc' ? 'neutral' : 'accent'"
                icon="i-lucide-arrow-up-down"
                @click="
                    $emit('change-sort', sortOrder === 'asc' ? 'desc' : 'asc')
                ">
                {{ sortOrder === "asc" ? "升序" : "降序" }}
            </UButton>
        </UTooltip>

        <USelect
            v-model="selectedTagsModel"
            :items="tagItems"
            multiple
            placeholder="篩選標籤"
            class="w-44" />

        <div class="min-w-[260px] flex items-center gap-2">
            <UInput
                v-model="keywordDraft"
                icon="i-lucide-search"
                placeholder="關鍵字（前綴）"
                @keyup.enter="emitSearch" />
            <UTooltip text="搜尋">
                <UButton
                    variant="ghost"
                    icon="i-lucide-search"
                    @click="emitSearch" />
            </UTooltip>
        </div>

        <UTooltip text="建立未命名筆記">
            <UButton
                color="accent"
                icon="i-lucide-plus"
                :loading="creating"
                @click="$emit('create')">
                Create
            </UButton>
        </UTooltip>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { toSelection } from "~/composables/utils/useFormat";
import type { Tag } from "~~/types/Tag";

const props = withDefaults(
    defineProps<{
        tags?: Tag[];
        selectedTags?: string[] | null;
        keyword?: string;
        starOnly?: boolean;
        sortOrder?: "asc" | "desc";
        creating?: boolean;
    }>(),
    {
        tags: () => [],
        selectedTags: () => [],
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
    (e: "change-tag", value: string[] | null): void;
    (e: "change-sort", value: "asc" | "desc"): void;
    (e: "search", value: string): void;
}>();

const tagItems = computed(() => toSelection(props.tags, "title", "id"));

/** tags：這是 state sync（適合用 computed get/set） */
const selectedTagsModel = computed<string[]>({
    get: () => props.selectedTags ?? [],
    set: (value) => emit("change-tag", value?.length ? value : null),
});

const keywordDraft = ref(props.keyword ?? "");
watch(
    () => props.keyword,
    (v) => {
        keywordDraft.value = v ?? "";
    }
);

function emitSearch() {
    emit("search", keywordDraft.value);
}
</script>

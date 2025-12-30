<template>
    <UCard
        class="h-full bg-slate-900/60 border-slate-800 hover:border-accent/50 transition-colors">
        <div class="flex items-start justify-between gap-3">
            <div class="space-y-1 min-w-0">
                <p class="font-semibold text-lg leading-tight line-clamp-2">
                    {{ title }}
                </p>
                <p v-if="description" class="text-xs text-slate-400 line-clamp-2">
                    {{ description }}
                </p>
            </div>
            <UTooltip text="收藏">
                <UButton
                    :color="starred ? 'yellow' : 'neutral'"
                    variant="ghost"
                    size="sm"
                    :icon="
                        starred ? 'i-lucide-star' : 'i-lucide-star-off'
                    "
                    @click.stop="$emit('toggle-star', id, !starred)" />
            </UTooltip>
        </div>

        <div
            class="mt-3 flex flex-wrap gap-2 max-h-16 overflow-hidden content-start">
            <UBadge
                v-for="tag in tags"
                :key="tag.id"
                variant="soft"
                color="primary"
                class="max-w-[5.5rem] truncate">
                {{ tag.title }}
            </UBadge>
        </div>

        <div class="mt-4 flex items-center justify-between text-xs text-slate-400">
            <span v-if="updatedAt">更新 {{ formattedUpdatedAt }}</span>

            <div class="flex items-center gap-2 ml-auto">
                <UTooltip text="刪除筆記">
                    <UButton
                        icon="i-lucide-trash-2"
                        color="red"
                        variant="ghost"
                        size="sm"
                        @click.stop="$emit('delete', id)">
                    </UButton>
                </UTooltip>
                <UButton
                    variant="soft"
                    color="accent"
                    size="sm"
                    icon-right="i-lucide-arrow-right"
                    @click="$emit('open', id)">
                    開啟
                </UButton>
            </div>
        </div>
    </UCard>
</template>
<script lang="ts" setup>
import { computed } from "vue";
import type { Tag } from "~~/types/Tag";

const props = withDefaults(
    defineProps<{
        id: string;
        title: string;
        tags?: Tag[];
        description?: string;
        starred?: boolean;
        updatedAt?: string | Date;
    }>(),
    {
        tags: () => [],
        description: "",
        starred: false,
        updatedAt: undefined,
    }
);

defineEmits<{
    (e: "toggle-star", id: string, nextValue: boolean): void;
    (e: "delete", id: string): void;
    (e: "open", id: string): void;
}>();

const formattedUpdatedAt = computed(() => {
    if (!props.updatedAt) return "";
    const date =
        props.updatedAt instanceof Date
            ? props.updatedAt
            : new Date(props.updatedAt);
    return date.toLocaleDateString("zh-TW", {
        month: "short",
        day: "2-digit",
    });
});
</script>

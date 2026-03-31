<template>
    <UCard
        class="min-w-75 h-48 bg-transparent hover:border-accent/50 transition-colors"
        :ui="{ body: 'h-full' }">
        <div class="flex flex-col h-full">
            <!-- Title + Star -->
            <div class="flex items-start justify-between gap-3">
                <div class="space-y-1 min-w-0">
                    <p
                        class="font-semibold text-lg leading-tight line-clamp-2 cursor-pointer hover:text-accent transition"
                        @click="$emit('open', id)">
                        {{ title }}
                    </p>
                    <p
                        v-if="description"
                        v-html="description"
                        class="text-xs text-slate-400 line-clamp-2 my-3"></p>
                </div>
                <UTooltip text="收藏">
                    <UButton
                        color="neutral"
                        variant="ghost"
                        size="sm"
                        class="hover:text-yellow-300"
                        @click.stop="$emit('toggle-star', id, !starred)">
                        <UIcon
                            :name="starred ? 'uis-star' : 'uil-star'"
                            class="w-4 h-4"
                            :class="
                                starred ? 'text-yellow-400' : 'text-slate-300'
                            " />
                    </UButton>
                </UTooltip>
            </div>

            <!-- Spacer - pushes tags and bottom row to bottom -->
            <div class="flex-1"></div>

            <!-- Tags - always just above date row -->
            <div
                class="mb-1 flex flex-wrap gap-2 content-start min-h-7 max-h-16 overflow-hidden">
                <UBadge
                    v-for="tag in tags"
                    :key="tag.id"
                    variant="soft"
                    color="primary"
                    class="max-w-[5.5rem] truncate">
                    {{ tag.title }}
                </UBadge>
            </div>

            <!-- Date + Buttons - pinned to bottom, 4px gap above -->
            <div
                class="mt-1 flex items-center justify-between text-xs text-slate-400">
                <div class="flex gap-x-2">
                    <UIcon name="i-lucide-clock-3" class="w-4 h-4" />
                    <span v-if="updatedAt">更新 {{ formattedUpdatedAt }}</span>
                </div>

                <div class="flex items-center gap-2 ml-auto">
                    <UTooltip text="刪除筆記">
                        <UButton
                            icon="i-lucide-trash-2"
                            color="red"
                            variant="ghost"
                            size="sm"
                            @click.stop="confirmDelete"></UButton>
                    </UTooltip>
                    <UButton
                        variant="soft"
                        color="accent"
                        size="sm"
                        icon-right="i-lucide-arrow-right"
                        @click="$emit('open', id)">
                        查看
                    </UButton>
                </div>
            </div>
        </div>
    </UCard>
</template>
<script lang="ts" setup>
import { computed } from "vue";
import type { Tag } from "~~/types/Tag";
import { useDialogs } from "~/composables/useDialogs";

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
    },
);

const emit = defineEmits<{
    (e: "toggle-star", id: string, nextValue: boolean): void;
    (e: "delete", id: string): void;
    (e: "open", id: string): void;
}>();

const { confirm } = useDialogs();

function confirmDelete() {
    confirm(
        `確定要刪除筆記「${props.title}」？此操作無法復原。`,
        undefined,
        () => emit("delete", props.id),
    );
}

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

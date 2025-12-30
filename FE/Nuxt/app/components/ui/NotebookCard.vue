<template>
    <UCard
        class="h-full bg-slate-900/60 border-slate-800 hover:border-accent/60 transition-colors">
        <div class="flex items-start justify-between gap-2">
            <div class="flex flex-col gap-1 min-w-0">
                <div class="flex items-center gap-2">
                    <p class="font-semibold text-lg truncate">
                        {{ title }}
                    </p>
                    <UBadge
                        v-if="collab"
                        color="primary"
                        variant="soft"
                        size="xs">
                        共編
                    </UBadge>
                </div>
                <p class="text-sm text-slate-400 line-clamp-2">
                    {{ description || "尚未填寫描述" }}
                </p>
            </div>
            <slot name="actions">
                <UButton
                    icon="i-lucide-arrow-up-right"
                    variant="ghost"
                    size="xs"
                    @click.stop="$emit('open', id)" />
            </slot>
        </div>

        <div class="mt-3 flex flex-wrap items-center gap-4 text-xs text-slate-400">
            <div class="flex items-center gap-1">
                <UIcon name="i-lucide-notebook-pen" class="w-4 h-4" />
                <span>{{ noteCount ?? 0 }} 筆記</span>
            </div>
            <div v-if="owner" class="flex items-center gap-1">
                <UIcon name="i-lucide-user" class="w-4 h-4" />
                <span class="truncate">{{ owner }}</span>
            </div>
            <div v-if="updatedAt" class="flex items-center gap-1">
                <UIcon name="i-lucide-clock-3" class="w-4 h-4" />
                <span>{{ formattedUpdatedAt }}</span>
            </div>
        </div>

        <div class="mt-4 flex items-center justify-between gap-3">
            <UButton
                v-if="deletable"
                color="red"
                variant="ghost"
                size="sm"
                icon="i-lucide-trash-2"
                class="justify-start"
                @click.stop="$emit('delete', id)">
                刪除
            </UButton>

            <div class="flex-1" />

            <UButton
                variant="soft"
                color="accent"
                size="sm"
                icon-right="i-lucide-arrow-right"
                @click="$emit('open', id)">
                查看
            </UButton>
        </div>
    </UCard>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = withDefaults(
    defineProps<{
        id: string;
        title: string;
        description?: string;
        noteCount?: number;
        updatedAt?: string | Date;
        owner?: string;
        collab?: boolean;
        deletable?: boolean;
    }>(),
    {
        description: "",
        noteCount: 0,
        updatedAt: undefined,
        owner: "",
        collab: false,
        deletable: true,
    }
);

defineEmits<{
    (e: "open", id: string): void;
    (e: "delete", id: string): void;
}>();

const formattedUpdatedAt = computed(() => {
    if (!props.updatedAt) return "";
    const date = props.updatedAt instanceof Date
        ? props.updatedAt
        : new Date(props.updatedAt);
    return date.toLocaleDateString("zh-TW", {
        month: "short",
        day: "2-digit",
    });
});
</script>

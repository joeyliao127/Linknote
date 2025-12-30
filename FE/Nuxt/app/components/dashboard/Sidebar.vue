<template>
    <div class="flex flex-col gap-6 p-4 w-full">
        <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
                <div
                    class="h-10 w-10 rounded-xl bg-accent/20 text-accent flex items-center justify-center font-bold">
                    Ln
                </div>
                <div>
                    <p class="text-base font-semibold">Linknote</p>
                    <p class="text-xs text-slate-400">知識網絡</p>
                </div>
            </div>
            <UButton
                icon="i-lucide-plus"
                color="accent"
                variant="soft"
                size="sm"
                aria-label="新增筆記本"
                @click="$emit('create')" />
        </div>

        <nav class="flex flex-col gap-2">
            <NuxtLink to="/index">
                <UButton
                    block
                    variant="ghost"
                    icon="i-lucide-activity"
                    class="justify-start">
                    概覽
                </UButton>
            </NuxtLink>
            <NuxtLink to="/notebooks">
                <UButton
                    block
                    variant="ghost"
                    icon="i-lucide-notebook-tabs"
                    class="justify-start">
                    我的筆記本
                </UButton>
            </NuxtLink>
            <NuxtLink to="/settings">
                <UButton
                    block
                    variant="ghost"
                    icon="i-lucide-settings-2"
                    class="justify-start">
                    設定
                </UButton>
            </NuxtLink>
        </nav>

        <div class="space-y-3">
            <div class="flex items-center justify-between">
                <p class="text-xs uppercase tracking-wider text-slate-400">
                    最近的筆記本
                </p>
                <UTooltip text="新增筆記本">
                    <UButton
                        icon="i-lucide-plus"
                        size="2xs"
                        variant="ghost"
                        color="accent"
                        @click="$emit('create')" />
                </UTooltip>
            </div>

            <div
                class="rounded-2xl border border-slate-800 bg-slate-900/60 divide-y divide-slate-800 overflow-hidden">
                <button
                    v-for="item in notebooks"
                    :key="item.id"
                    class="w-full px-4 py-3 text-left flex items-center justify-between gap-2 hover:bg-slate-800/80 transition"
                    :class="{
                        'bg-slate-800/80 border-l-2 border-accent text-accent':
                            item.id === currentId,
                    }"
                    @click="$emit('select', item.id)">
                    <div class="flex flex-col gap-1 min-w-0">
                        <div class="flex items-center gap-2">
                            <span class="font-medium truncate">{{ item.title }}</span>
                            <UBadge
                                v-if="item.collab"
                                variant="soft"
                                color="primary"
                                size="xs">
                                共編
                            </UBadge>
                        </div>
                        <p
                            v-if="item.updatedAt"
                            class="text-xs text-slate-400 truncate">
                            {{ formatDate(item.updatedAt) }}
                        </p>
                    </div>
                    <UIcon
                        name="i-lucide-chevron-right"
                        class="w-4 h-4 text-slate-500" />
                </button>

                <div
                    ref="loadMoreTrigger"
                    class="h-11 flex items-center justify-center text-xs text-slate-400">
                    <UIcon
                        v-if="loading"
                        name="i-lucide-loader-2"
                        class="w-4 h-4 animate-spin" />
                    <button
                        v-else-if="hasMore"
                        class="flex items-center gap-1 hover:text-accent transition"
                        @click="$emit('load-more')">
                        <UIcon
                            name="i-lucide-chevron-down"
                            class="w-4 h-4" />
                        捲動或點擊載入更多
                    </button>
                    <span v-else class="text-slate-600">沒有更多筆記本</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";

export interface NotebookNavItem {
    id: string;
    title: string;
    updatedAt?: string | Date;
    collab?: boolean;
}

const props = withDefaults(
    defineProps<{
        notebooks: NotebookNavItem[];
        loading?: boolean;
        hasMore?: boolean;
        currentId?: string;
    }>(),
    {
        notebooks: () => [],
        loading: false,
        hasMore: false,
        currentId: undefined,
    }
);

const emit = defineEmits<{
    (e: "create"): void;
    (e: "select", id: string): void;
    (e: "load-more"): void;
}>();

const loadMoreTrigger = ref<HTMLElement | null>(null);
let observer: IntersectionObserver | null = null;

const formatDate = (value?: string | Date) => {
    if (!value) return "";
    const date = value instanceof Date ? value : new Date(value);
    return date.toLocaleDateString("zh-TW", {
        month: "short",
        day: "2-digit",
    });
};

onMounted(() => {
    observer = new IntersectionObserver(
        (entries) => {
            const [entry] = entries;
            if (
                entry?.isIntersecting &&
                props.hasMore &&
                !props.loading
            ) {
                emit("load-more");
            }
        },
        { threshold: 1 }
    );

    if (loadMoreTrigger.value) {
        observer.observe(loadMoreTrigger.value);
    }
});

onBeforeUnmount(() => {
    if (observer) {
        observer.disconnect();
        observer = null;
    }
});
</script>

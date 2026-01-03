<template>
    <div class="flex flex-col h-full w-full bg-slate-900/70 text-slate-100">
        <div class="flex items-center justify-between p-4">
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
                @click="emit('create')" />
        </div>

        <div class="px-2">
            <nav
                class="flex flex-col gap-1 rounded-2xl border border-slate-800 bg-slate-950/60 p-2">
                <NuxtLink to="/">
                    <UButton
                        block
                        variant="ghost"
                        icon="i-lucide-activity"
                        class="justify-start">
                        概覽
                    </UButton>
                </NuxtLink>
            </nav>
        </div>

        <div class="flex-1 px-2 mt-4 overflow-y-auto space-y-3 pr-1 pb-4">
            <div
                class="rounded-2xl border border-slate-800 bg-slate-950/60 overflow-hidden">
                <div
                    class="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-800/50 transition cursor-pointer"
                    @click="emit('go-notebooks');dashboardNav?.goNotebooks && dashboardNav.goNotebooks();">
                    <div class="flex items-center gap-2">
                        <UIcon name="i-lucide-notebook-pen" class="w-4 h-4" />
                        <span class="font-semibold">我的筆記本</span>
                    </div>
                    <UIcon
                        :name="isMyOpen ? 'i-lucide-chevron-down' : 'i-lucide-chevron-right'"
                        class="w-4 h-4 transition text-slate-400 hover:text-accent"
                        @click.stop="isMyOpen = !isMyOpen" />
                </div>

                <transition name="fade">
                    <div v-if="isMyOpen" class="divide-y divide-slate-800">
                        <template
                            v-for="item in props.notebooks"
                            :key="item.id">
                            <button
                                class="w-full px-4 py-3 text-left flex items-center justify-between gap-2 hover:bg-slate-800/80 transition"
                                :class="{
                                    'bg-slate-800/80 border-l-2 border-accent text-accent':
                                        item.id === props.currentId,
                                }"
                                @click="emit('select', item.id)">
                                <div class="flex flex-col gap-1 min-w-0">
                                    <div class="flex items-center gap-2">
                                        <span class="font-medium truncate">
                                            {{ item.title }}
                                        </span>
                                    </div>
                                    <p
                                        v-if="item.updatedAt"
                                        class="text-xs text-slate-400 truncate">
                                        {{ formatDate(item.updatedAt) }}
                                    </p>
                                </div>
                                <UBadge
                                    variant="soft"
                                    color="primary"
                                    size="xs">
                                    {{ item.noteCount ?? 0 }}
                                </UBadge>
                            </button>
                        </template>

                        <div
                            ref="myLoadMoreTrigger"
                            class="h-11 flex items-center justify-center text-xs text-slate-400">
                            <UIcon
                                v-if="props.loading"
                                name="i-lucide-loader-2"
                                class="w-4 h-4 animate-spin" />
                            <button
                                v-else-if="props.hasMore"
                                class="flex items-center gap-1 hover:text-accent transition"
                                @click="emit('load-more')">
                                <UIcon
                                    name="i-lucide-chevron-down"
                                    class="w-4 h-4" />
                                載入更多
                            </button>
                            <span v-else class="text-slate-600">
                                沒有更多筆記本
                            </span>
                        </div>
                    </div>
                </transition>
            </div>

            <div
                class="rounded-2xl border border-slate-800 bg-slate-950/60 overflow-hidden">
                <div
                    class="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-800/50 transition cursor-pointer"
                    @click="emit('go-co-notebooks');dashboardNav?.goCoNotebooks && dashboardNav.goCoNotebooks();">
                    <div class="flex items-center gap-2">
                        <UIcon name="i-lucide-users" class="w-4 h-4" />
                        <span class="font-semibold">共編筆記本</span>
                    </div>
                    <UIcon
                        :name="isCoOpen ? 'i-lucide-chevron-down' : 'i-lucide-chevron-right'"
                        class="w-4 h-4 transition text-slate-400 hover:text-accent"
                        @click.stop="isCoOpen = !isCoOpen" />
                </div>

                <transition name="fade">
                    <div v-if="isCoOpen" class="divide-y divide-slate-800">
                        <template
                            v-for="item in props.coNotebooks"
                            :key="item.id">
                            <button
                                class="w-full px-4 py-3 text-left flex items-center justify-between gap-2 hover:bg-slate-800/80 transition"
                                :class="{
                                    'bg-slate-800/80 border-l-2 border-accent text-accent':
                                        item.id === props.currentCoId,
                                }"
                                @click="emit('select-co', item.id)">
                                <div class="flex flex-col gap-1 min-w-0">
                                    <div class="flex items-center gap-2">
                                        <span class="font-medium truncate">
                                            {{ item.title }}
                                        </span>
                                    </div>
                                    <p
                                        v-if="item.updatedAt"
                                        class="text-xs text-slate-400 truncate">
                                        {{ formatDate(item.updatedAt) }}
                                    </p>
                                </div>
                                <UBadge
                                    variant="soft"
                                    color="primary"
                                    size="xs">
                                    {{ item.noteCount ?? 0 }}
                                </UBadge>
                            </button>
                        </template>

                        <div
                            ref="coLoadMoreTrigger"
                            class="h-11 flex items-center justify-center text-xs text-slate-400">
                            <UIcon
                                v-if="props.coLoading"
                                name="i-lucide-loader-2"
                                class="w-4 h-4 animate-spin" />
                            <button
                                v-else-if="props.coHasMore"
                                class="flex items-center gap-1 hover:text-accent transition"
                                @click="emit('load-more-co')">
                                <UIcon
                                    name="i-lucide-chevron-down"
                                    class="w-4 h-4" />
                                載入更多
                            </button>
                            <span v-else class="text-slate-600">
                                沒有更多筆記本
                            </span>
                        </div>
                    </div>
                </transition>
            </div>
        </div>

        <div class="mt-auto p-4 space-y-2 border-t border-slate-800">
            <SettingsModal
                v-model="settingsOpen"
                :sections="props.settingsSections"
                :active="activeSettings"
                @change="handleSettingsChange"
                @close="settingsOpen = false">
                <template #content="{ section }">
                    <slot name="settings-content" :section="section">
                        <p class="text-sm text-slate-400">
                            此處預留設定表單內容（由外部頁面注入或後續補充）。
                        </p>
                    </slot>
                </template>
                <template #footer="{ close }">
                    <slot name="settings-footer" :close="close" />
                </template>
            </SettingsModal>

            <UButton
                block
                variant="ghost"
                color="red"
                icon="i-lucide-log-out"
                class="justify-start"
                @click="emit('logout')">
                登出
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
import { inject, onBeforeUnmount, onMounted, ref, computed, watchEffect } from "vue";
import SettingsModal from "./SettingsModal.vue";

export interface NotebookNavItem {
    id: string;
    title: string;
    updatedAt?: string | Date;
    collab?: boolean;
    noteCount?: number;
}

const props = withDefaults(
    defineProps<{
        notebooks: NotebookNavItem[];
        coNotebooks: NotebookNavItem[];
        loading?: boolean;
        hasMore?: boolean;
        coLoading?: boolean;
        coHasMore?: boolean;
        currentId?: string;
        currentCoId?: string;
        settingsSections?: { label: string; value: string }[];
    }>(),
    {
        notebooks: () => [],
        coNotebooks: () => [],
        loading: false,
        hasMore: false,
        coLoading: false,
        coHasMore: false,
        currentId: undefined,
        currentCoId: undefined,
        settingsSections: () => [
            { label: "個人資料", value: "profile" },
            { label: "帳號安全", value: "security" },
            { label: "通知", value: "notification" },
        ],
    }
);

const emit = defineEmits<{
    (e: "create"): void;
    (e: "select", id: string): void;
    (e: "load-more"): void;
    (e: "select-co", id: string): void;
    (e: "load-more-co"): void;
    (e: "logout"): void;
    (e: "go-notebooks"): void;
    (e: "go-co-notebooks"): void;
}>();

const dashboardNav = inject<{
    goNotebooks?: () => void;
    goCoNotebooks?: () => void;
} | null>("dashboard-nav", null);

const isMyOpen = ref(false);
const isCoOpen = ref(false);
const settingsOpen = ref(false);
const activeSettings = ref(props.settingsSections[0]?.value ?? "profile");

function handleSettingsChange(value: string) {
    activeSettings.value = value;
}

const myLoadMoreTrigger = ref<HTMLElement | null>(null);
const coLoadMoreTrigger = ref<HTMLElement | null>(null);

let myObserver: IntersectionObserver | null = null;
let coObserver: IntersectionObserver | null = null;

const formatDate = (value?: string | Date) => {
    if (!value) return "";
    const date = value instanceof Date ? value : new Date(value);
    return date.toLocaleDateString("zh-TW", {
        month: "short",
        day: "2-digit",
    });
};

onMounted(() => {
    myObserver = new IntersectionObserver(
        (entries) => {
            const [entry] = entries;
            if (entry?.isIntersecting && props.hasMore && !props.loading) {
                emit("load-more");
            }
        },
        { threshold: 1 }
    );

    coObserver = new IntersectionObserver(
        (entries) => {
            const [entry] = entries;
            if (entry?.isIntersecting && props.coHasMore && !props.coLoading) {
                emit("load-more-co");
            }
        },
        { threshold: 1 }
    );

    if (myLoadMoreTrigger.value) {
        myObserver.observe(myLoadMoreTrigger.value);
    }
    if (coLoadMoreTrigger.value) {
        coObserver.observe(coLoadMoreTrigger.value);
    }
});

onBeforeUnmount(() => {
    if (myObserver) {
        myObserver.disconnect();
        myObserver = null;
    }
    if (coObserver) {
        coObserver.disconnect();
        coObserver = null;
    }
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
    opacity: 0;
    transform: translateY(-4px);
}
</style>

<template>
    <div class="flex flex-col h-full w-full text-slate-200/90 text-sm">
        <!-- ── 頂部：Hi user ──────────────────────────── -->
        <div class="px-4 py-3 mt-2">
            <div
                class="flex flex-col gap-y-2 justify-center px-4 py-6 bg-black/[0.3] rounded">
                <div class="flex justify-center items-center gap-3">
                    <UIcon
                        name="i-lucide-user-circle"
                        class="w-8 h-8 text-slate-200/70 shrink-0" />
                    <p class="text-2xl font-semibold text-white leading-tight">
                        Hi {{ userInfo?.username || username }}
                    </p>
                </div>
                <p
                    class="text-base text-center font-semibold text-white leading-tight">
                    Welcome to Linknote !
                </p>
            </div>
        </div>

        <!-- ── 新增按鈕 ───────────────────────────────── -->
        <div class="px-4 pb-4">
            <UButton
                block
                color="primary"
                icon="i-lucide-plus"
                class="text-white text-lg font-semibold h-12"
                @click="onCreate">
                New Notebook
            </UButton>
        </div>

        <!-- ── 導航區塊（剩餘高度） ─────────────────── -->
        <nav
            class="flex-1 overflow-y-auto border-y border-white/[0.08] py-2 flex flex-col">
            <!-- My Notebooks -->
            <div class="flex flex-col">
                <button
                    class="flex items-center gap-2.5 w-full px-5 py-2.5 text-left text-white bg-transparent border-none cursor-pointer transition-colors hover:bg-white/[0.06]"
                    @click="onGoNotebooks">
                    <UIcon
                        name="i-lucide-notebook-pen"
                        class="w-4 h-4 shrink-0 text-slate-200/60" />
                    <span class="flex-1 font-medium">My Notebooks</span>
                    <UIcon
                        :name="
                            isMyOpen
                                ? 'i-lucide-chevron-down'
                                : 'i-lucide-chevron-right'
                        "
                        class="w-3.5 h-3.5 text-slate-200/45 shrink-0"
                        @click.stop="isMyOpen = !isMyOpen" />
                </button>
                <transition name="slide">
                    <div v-if="isMyOpen" class="overflow-hidden">
                        <UIcon
                            v-if="loading"
                            name="i-lucide-loader-2"
                            class="w-3.5 h-3.5 animate-spin mx-auto my-2 block" />
                        <template v-else-if="notebooks.length > 0">
                            <button
                                v-for="nb in notebooks"
                                :key="nb.id"
                                class="block w-full py-[0.45rem] pl-11 pr-5 text-left text-xs text-slate-200/65 bg-transparent border-none cursor-pointer whitespace-nowrap overflow-hidden text-ellipsis transition-colors hover:bg-white/5 hover:text-white"
                                :class="
                                    nb.id === currentId
                                        ? 'text-primary border-l-2 border-primary pl-[calc(2.75rem-2px)]'
                                        : ''
                                "
                                @click="onSelect(nb.id)">
                                {{ nb.title }}
                            </button>
                        </template>
                        <p
                            v-else
                            class="py-[0.45rem] pl-11 pr-5 text-[0.78rem] text-slate-200/35 italic">
                            尚無筆記本
                        </p>
                    </div>
                </transition>
            </div>

            <!-- Collaborative Notebooks -->
            <div class="flex flex-col">
                <button
                    class="flex items-center gap-2.5 w-full px-5 py-2.5 text-left text-white bg-transparent border-none cursor-pointer transition-colors hover:bg-white/[0.06]"
                    @click="onGoCoNotebooks">
                    <UIcon
                        name="i-lucide-users"
                        class="w-4 h-4 shrink-0 text-slate-200/60" />
                    <span class="flex-1 font-medium">
                        Collaborative Notebooks
                    </span>
                    <UIcon
                        :name="
                            isCoOpen
                                ? 'i-lucide-chevron-down'
                                : 'i-lucide-chevron-right'
                        "
                        class="w-3.5 h-3.5 text-slate-200/45 shrink-0"
                        @click.stop="isCoOpen = !isCoOpen" />
                </button>
                <transition name="slide">
                    <div v-if="isCoOpen" class="overflow-hidden">
                        <p
                            class="py-[0.45rem] pl-11 pr-5 text-[0.78rem] text-slate-200/35 italic">
                            尚無筆記本
                        </p>
                    </div>
                </transition>
            </div>

            <!-- Invitations Management -->
            <NuxtLink
                to="/invitations"
                class="flex items-center gap-2.5 w-full px-5 py-2.5 text-white no-underline transition-colors hover:bg-white/[0.06]">
                <UIcon
                    name="i-lucide-mail"
                    class="w-4 h-4 shrink-0 text-slate-200/60" />
                <span class="flex-1 font-medium">Invitations Management</span>
            </NuxtLink>

            <!-- RAG 知識庫 -->
            <NuxtLink
                to="/rag"
                class="flex items-center gap-2.5 w-full px-5 py-2.5 text-white no-underline transition-colors hover:bg-white/[0.06]">
                <UIcon
                    name="i-lucide-brain"
                    class="w-4 h-4 shrink-0 text-slate-200/60" />
                <span class="flex-1 font-medium">RAG 知識庫</span>
            </NuxtLink>

            <!-- Setting -->
            <div class="sb-settings-wrap">
                <SettingsModal
                    v-model="settingsOpen"
                    :sections="settingsSections"
                    :active="activeSettings"
                    @change="activeSettings = $event"
                    @close="settingsOpen = false">
                    <template #content="{ section }">
                        <slot name="settings-content" :section="section">
                            <p class="text-sm text-slate-400">
                                此處預留設定表單內容。
                            </p>
                        </slot>
                    </template>
                    <template #footer="{ close }">
                        <slot name="settings-footer" :close="close" />
                    </template>
                </SettingsModal>
            </div>
        </nav>

        <!-- ── 底部：email + 登出 ──────────────────── -->
        <div class="px-4 py-3 flex flex-col gap-2">
            <div class="flex items-center gap-2 px-3 py-2 bg-white/5 rounded">
                <span class="w-2 h-2 rounded-full bg-primary shrink-0" />
                <span
                    class="text-[0.8rem] text-slate-200/75 overflow-hidden text-ellipsis whitespace-nowrap">
                    {{ userInfo?.email || userEmail }}
                </span>
            </div>
            <UButton
                block
                color="primary"
                icon="i-lucide-log-out"
                class="text-white font-semibold"
                @click="onLogout">
                SignOut
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useAuth, navigateTo, useRoute, useFetch } from "#imports";
import { useNotebookNav } from "~/composables/useNotebookNav";
import SettingsModal from "./SettingsModal.vue";

const props = withDefaults(
    defineProps<{
        settingsSections?: {
            label: string;
            value: string;
            description?: string;
            icon?: string;
        }[];
    }>(),
    {
        settingsSections: () => [
            { label: "個人資料", value: "profile", icon: "i-lucide-user" },
            { label: "帳號安全", value: "security", icon: "i-lucide-shield" },
            { label: "通知", value: "notification", icon: "i-lucide-bell" },
        ],
    },
);

const emit = defineEmits<{
    (e: "logout"): void;
}>();

// ── Auth ──────────────────────────────────────────────
const { data, signOut } = useAuth();
const userEmail = computed(() => (data.value as any)?.user?.email ?? "");
const username = computed(() => userEmail.value.split("@")[0] ?? "user");

// ── User info from BFF ────────────────────────────────
const { data: userInfo } = useFetch<{
    userId: string;
    email: string;
    username: string;
}>("/api/user/token");

// ── Notebooks（shared state via useState, no extra fetch） ──
const { items: notebooks, loading, fetchNotebooks } = useNotebookNav();

// ── 路由推導 currentId ────────────────────────────────
const route = useRoute();
const currentId = computed(() => route.params.id as string | undefined);

// ── Accordion state ───────────────────────────────────
const isMyOpen = ref(false);
const isCoOpen = ref(false);

// ── Navigation ────────────────────────────────────────
function onGoNotebooks() {
    navigateTo("/notebooks");
    isMyOpen.value = !isMyOpen.value;
}

function onGoCoNotebooks() {
    navigateTo("/co-notebooks");
    isCoOpen.value = !isCoOpen.value;
}

function onSelect(id: string) {
    navigateTo(`/notebooks/${id}/notes`);
}

function onCreate() {
    navigateTo("/notebooks/create");
}

async function onLogout() {
    emit("logout");
    await signOut({ callbackUrl: "/signIn" });
}

// ── Settings ──────────────────────────────────────────
const settingsOpen = ref(false);
const activeSettings = ref(props.settingsSections[0]?.value ?? "profile");

// ── Init：使用 useState cache，initialized 時不重複 fetch ─
onMounted(() => {
    fetchNotebooks();
});
</script>

<style scoped>
/* ── Slide transition for accordion ─────────────────── */
.slide-enter-active,
.slide-leave-active {
    transition:
        max-height 0.22s ease,
        opacity 0.18s ease;
    max-height: 400px;
}

.slide-enter-from,
.slide-leave-to {
    max-height: 0;
    opacity: 0;
}

/* ── SettingsModal trigger (custom component, needs deep) */
.sb-settings-wrap :deep(button) {
    display: flex;
    align-items: center;
    gap: 0.625rem;
    width: 100%;
    padding: 0.6rem 1.25rem;
    text-align: left;
    color: #ffffff;
    background: none;
    border: none;
    border-radius: 0;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: background 120ms ease;
}

.sb-settings-wrap :deep(button:hover) {
    background: rgba(255, 255, 255, 0.06);
}
</style>

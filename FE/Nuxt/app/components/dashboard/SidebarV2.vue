<template>
    <div class="sb">
        <!-- ── 頂部：Hi user ──────────────────────────── -->
        <div class="sb-header-wrap">
            <div class="sb-header">
                <UIcon name="i-lucide-user-circle" class="sb-avatar" />
                <div class="sb-greeting">
                    <p class="sb-hi">Hi {{ username }}</p>
                    <p class="sb-welcome">Welcome to LinkNote!</p>
                </div>
            </div>
        </div>

        <!-- ── 新增按鈕 ───────────────────────────────── -->
        <div class="sb-create">
            <UButton
                block
                class="sb-create-btn"
                icon="i-lucide-plus"
                @click="onCreate">
                New Notebook
            </UButton>
        </div>

        <!-- ── 導航區塊（剩餘高度） ─────────────────── -->
        <nav class="sb-nav">
            <!-- My Notebooks -->
            <div class="sb-nav-group">
                <button class="sb-nav-row" @click="onGoNotebooks">
                    <UIcon name="i-lucide-notebook-pen" class="sb-nav-icon" />
                    <span class="sb-nav-label">My Notebooks</span>
                    <UIcon
                        :name="isMyOpen ? 'i-lucide-chevron-down' : 'i-lucide-chevron-right'"
                        class="sb-nav-arrow"
                        @click.stop="isMyOpen = !isMyOpen" />
                </button>
                <transition name="slide">
                    <div v-if="isMyOpen" class="sb-accordion">
                        <UIcon
                            v-if="loading"
                            name="i-lucide-loader-2"
                            class="w-3.5 h-3.5 animate-spin mx-auto my-2 block" />
                        <template v-else-if="notebooks.length > 0">
                            <button
                                v-for="nb in notebooks"
                                :key="nb.id"
                                class="sb-notebook-item"
                                :class="{ 'sb-notebook-active': nb.id === currentId }"
                                @click="onSelect(nb.id)">
                                {{ nb.title }}
                            </button>
                        </template>
                        <p v-else class="sb-empty">尚無筆記本</p>
                    </div>
                </transition>
            </div>

            <!-- Collaborative Notebooks -->
            <div class="sb-nav-group">
                <button class="sb-nav-row" @click="onGoCoNotebooks">
                    <UIcon name="i-lucide-users" class="sb-nav-icon" />
                    <span class="sb-nav-label">Collaborative Notebooks</span>
                    <UIcon
                        :name="isCoOpen ? 'i-lucide-chevron-down' : 'i-lucide-chevron-right'"
                        class="sb-nav-arrow"
                        @click.stop="isCoOpen = !isCoOpen" />
                </button>
                <transition name="slide">
                    <div v-if="isCoOpen" class="sb-accordion">
                        <p class="sb-empty">尚無筆記本</p>
                    </div>
                </transition>
            </div>

            <!-- Invitations Management -->
            <NuxtLink to="/invitations" class="sb-nav-row">
                <UIcon name="i-lucide-mail" class="sb-nav-icon" />
                <span class="sb-nav-label">Invitations Management</span>
            </NuxtLink>

            <!-- RAG 知識庫 -->
            <NuxtLink to="/rag" class="sb-nav-row">
                <UIcon name="i-lucide-brain" class="sb-nav-icon" />
                <span class="sb-nav-label">RAG 知識庫</span>
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
                            <p class="text-sm text-slate-400">此處預留設定表單內容。</p>
                        </slot>
                    </template>
                    <template #footer="{ close }">
                        <slot name="settings-footer" :close="close" />
                    </template>
                </SettingsModal>
            </div>
        </nav>

        <!-- ── 底部：email + 登出 ──────────────────── -->
        <div class="sb-footer">
            <div class="sb-email-row">
                <span class="sb-online-dot" />
                <span class="sb-email">{{ userEmail }}</span>
            </div>
            <UButton
                block
                class="sb-signout-btn"
                icon="i-lucide-log-out"
                @click="onLogout">
                SignOut
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useAuth, navigateTo, useRoute } from "#imports";
import { useNotebookNav } from "~/composables/useNotebookNav";
import SettingsModal from "./SettingsModal.vue";

const props = withDefaults(
    defineProps<{
        settingsSections?: { label: string; value: string; description?: string; icon?: string }[];
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
/* ── Shell ───────────────────────────────────────────── */
.sb {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
    background-color: var(--background, #1f221f);
    color: rgba(226, 232, 240, 0.9);
    font-size: 0.875rem;
}

/* ── Header ──────────────────────────────────────────── */
.sb-header-wrap {
    padding: 0.75rem 1rem;
}

.sb-header {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.875rem 1rem;
    background-color: rgba(255, 255, 255, 0.06);
    border-radius: 5px;
}

.sb-avatar {
    width: 2rem;
    height: 2rem;
    color: rgba(226, 232, 240, 0.7);
    flex-shrink: 0;
}

.sb-hi {
    font-size: 1rem;
    font-weight: 600;
    color: #ffffff;
    line-height: 1.3;
}

.sb-welcome {
    font-size: 0.75rem;
    color: rgba(226, 232, 240, 0.55);
    line-height: 1.3;
}

/* ── Create button ───────────────────────────────────── */
.sb-create {
    padding: 0.75rem 1rem;
}

.sb-create-btn {
    background: var(--primary) !important;
    color: #ffffff !important;
    font-weight: 600;
    border: none !important;
    transition: background 150ms ease;
}

.sb-create-btn:hover {
    background: var(--primary-dark) !important;
}

/* ── Nav area ────────────────────────────────────────── */
.sb-nav {
    flex: 1;
    overflow-y: auto;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    padding: 0.5rem 0;
    display: flex;
    flex-direction: column;
}

/* Shared nav row style (button + NuxtLink) */
.sb-nav-row {
    display: flex;
    align-items: center;
    gap: 0.625rem;
    width: 100%;
    padding: 0.6rem 1.25rem;
    text-align: left;
    color: #ffffff;
    background: none;
    border: none;
    cursor: pointer;
    transition: background 120ms ease;
    text-decoration: none;
}

.sb-nav-row:hover {
    background: rgba(255, 255, 255, 0.06);
}

.sb-nav-icon {
    width: 1rem;
    height: 1rem;
    flex-shrink: 0;
    color: rgba(226, 232, 240, 0.6);
}

.sb-nav-label {
    flex: 1;
    font-weight: 500;
}

.sb-nav-arrow {
    width: 0.875rem;
    height: 0.875rem;
    color: rgba(226, 232, 240, 0.45);
    flex-shrink: 0;
}

/* ── Accordion ───────────────────────────────────────── */
.sb-nav-group {
    display: flex;
    flex-direction: column;
}

.sb-accordion {
    overflow: hidden;
}

.sb-notebook-item {
    display: block;
    width: 100%;
    padding: 0.45rem 1.25rem 0.45rem 2.75rem;
    text-align: left;
    font-size: 0.8rem;
    color: rgba(226, 232, 240, 0.65);
    background: none;
    border: none;
    cursor: pointer;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: background 120ms ease, color 120ms ease;
}

.sb-notebook-item:hover {
    background: rgba(255, 255, 255, 0.05);
    color: #ffffff;
}

.sb-notebook-active {
    color: var(--primary);
    border-left: 2px solid var(--primary);
    padding-left: calc(2.75rem - 2px);
}

.sb-empty {
    padding: 0.45rem 1.25rem 0.45rem 2.75rem;
    font-size: 0.78rem;
    color: rgba(226, 232, 240, 0.35);
    font-style: italic;
}

/* ── SettingsModal trigger override ─────────────────── */
.sb-settings-wrap :deep(button) {
    display: flex;
    align-items: center;
    gap: 0.625rem;
    width: 100%;
    padding: 0.6rem 1.25rem;
    text-align: left;
    color: #ffffff !important;
    background: none !important;
    border: none !important;
    border-radius: 0 !important;
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: background 120ms ease;
}

.sb-settings-wrap :deep(button:hover) {
    background: rgba(255, 255, 255, 0.06) !important;
    color: #ffffff !important;
}

/* ── Footer ──────────────────────────────────────────── */
.sb-footer {
    padding: 0.75rem 1rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.sb-email-row {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 0.75rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 5px;
}

.sb-online-dot {
    width: 0.5rem;
    height: 0.5rem;
    border-radius: 50%;
    background: var(--primary);
    flex-shrink: 0;
}

.sb-email {
    font-size: 0.8rem;
    color: rgba(226, 232, 240, 0.75);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.sb-signout-btn {
    background: var(--primary) !important;
    color: #ffffff !important;
    font-weight: 600;
    border: none !important;
    transition: background 150ms ease;
}

.sb-signout-btn:hover {
    background: var(--primary-dark) !important;
}

/* ── Transition ──────────────────────────────────────── */
.slide-enter-active,
.slide-leave-active {
    transition: max-height 0.22s ease, opacity 0.18s ease;
    max-height: 400px;
}

.slide-enter-from,
.slide-leave-to {
    max-height: 0;
    opacity: 0;
}
</style>

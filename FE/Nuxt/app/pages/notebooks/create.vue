<template>
    <DashboardShell>
        <template #sidebar>
            <Sidebar
                :notebooks="notebooksNav"
                :co-notebooks="[]"
                :loading="notebooksLoading"
                :current-id="notebooksNav[0]?.id"
                :settings-sections="settingsSections"
                @load-more="fetchNotebooks"
                @create="goCreate"
                @go-notebooks="goNotebooks"
                @go-co-notebooks="goCoNotebooks" />
        </template>

        <template #header>
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm text-slate-400">筆記本</p>
                    <h1 class="text-2xl font-semibold">建立新的筆記本</h1>
                </div>
                <UButton variant="ghost" icon="i-lucide-arrow-left" @click="goBack">
                    返回
                </UButton>
            </div>
        </template>

        <div class="max-w-2xl">
            <UCard class="bg-slate-900/60 border-slate-800">
                <template #header>
                    <p class="font-semibold">填寫筆記本資訊</p>
                </template>

                <NotebookForm
                    :submitting="submitting"
                    submit-label="建立"
                    @submit="handleSubmit" />
            </UCard>
        </div>
    </DashboardShell>
</template>

<script setup lang="ts">
import { onMounted, useRouter, useAuth, useToast } from "#imports";
import DashboardShell from "~/components/dashboard/DashboardShell.vue";
import Sidebar from "~/components/dashboard/Sidebar.vue";
import NotebookForm from "~/components/notebook/NotebookForm.vue";
import { useNotebookNav } from "~/composables/useNotebookNav";
import { useNotebook } from "~/composables/model/useNotebook";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const toast = useToast();
const auth = useAuth();
const { items: notebooksNav, fetchNotebooks, loading: notebooksLoading } =
    useNotebookNav();
const { createNotebook } = useNotebook();

const settingsSections = [
    { label: "個人資料", value: "profile" },
    { label: "帳號安全", value: "security" },
    { label: "通知", value: "notification" },
];

const submitting = ref(false);

const userId = computed(() => auth.data.value?.user?.id || "");

async function handleSubmit(value: { title: string; description?: string; active?: boolean }) {
    if (submitting.value) return;
    submitting.value = true;
    try {
        await createNotebook(userId.value, {
            title: value.title,
            description: value.description,
            active: value.active,
        });
        toast.add({
            title: "建立成功",
            color: "accent",
        });
        router.push("/notebooks");
    } catch (error: any) {
        toast.add({
            title: "建立失敗",
            description: error?.data?.message || error?.message || "請稍後再試",
            color: "red",
        });
    } finally {
        submitting.value = false;
    }
}

function goBack() {
    router.back();
}

function goCreate() {
    router.push("/notebooks/create");
}

function goNotebooks() {
    router.push("/notebooks");
}

function goCoNotebooks() {
    router.push({ path: "/notebooks", query: { collab: "true" } });
}

onMounted(() => {
    fetchNotebooks();
});
</script>

<style scoped></style>

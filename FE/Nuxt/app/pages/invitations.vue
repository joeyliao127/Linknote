<template>
    <div class="p-6 space-y-6">
        <!-- Header -->
        <div>
            <p class="text-sm text-slate-400">協作</p>
            <h1 class="text-2xl font-semibold text-white">邀請管理</h1>
        </div>

        <!-- 收到的邀請 -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <template #header>
                <div class="flex items-center justify-between">
                    <p class="font-semibold">收到的邀請</p>
                    <UButton
                        variant="ghost"
                        icon="i-lucide-refresh-cw"
                        size="sm"
                        :loading="receivedLoading"
                        @click="loadReceived" />
                </div>
            </template>

            <UTable
                :data="received"
                :columns="receivedColumns"
                :loading="receivedLoading"
                class="w-full">
                <template #notebookTitle-cell="{ row }">
                    <span class="font-medium text-white">{{ row.original.notebookTitle ?? "—" }}</span>
                </template>
                <template #inviterName-cell="{ row }">
                    <span class="text-slate-300">{{ row.original.inviterName ?? "—" }}</span>
                </template>
                <template #message-cell="{ row }">
                    <span class="text-sm text-slate-400">{{ row.original.message || "—" }}</span>
                </template>
                <template #status-cell="{ row }">
                    <UBadge :color="statusColor(row.original.status)" variant="subtle">
                        {{ statusLabel(row.original.status) }}
                    </UBadge>
                </template>
                <template #action-cell="{ row }">
                    <div
                        v-if="row.original.status === 'pending' || row.original.status === 'resend'"
                        class="flex items-center gap-2">
                        <UButton
                            size="xs"
                            color="primary"
                            class="text-white"
                            :loading="actionId === row.original.id"
                            @click="handleUpdate(row.original.id, 'accept')">
                            接受
                        </UButton>
                        <UButton
                            size="xs"
                            color="neutral"
                            variant="ghost"
                            class="border border-white/20"
                            :loading="actionId === row.original.id"
                            @click="handleUpdate(row.original.id, 'reject')">
                            拒絕
                        </UButton>
                    </div>
                </template>
            </UTable>
        </UCard>

        <!-- 寄出的邀請 -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <template #header>
                <div class="flex items-center justify-between">
                    <p class="font-semibold">寄出的邀請</p>
                    <div class="flex items-center gap-3">
                        <UButton
                            variant="ghost"
                            icon="i-lucide-refresh-cw"
                            size="sm"
                            :loading="sentLoading"
                            @click="loadSent" />
                        <UButton
                            color="primary"
                            class="text-white"
                            icon="i-lucide-send"
                            @click="openCreate = true">
                            發送邀請
                        </UButton>
                    </div>
                </div>
            </template>

            <UTable
                :data="sent"
                :columns="sentColumns"
                :loading="sentLoading"
                class="w-full">
                <template #notebookTitle-cell="{ row }">
                    <span class="font-medium text-white">{{ row.original.notebookTitle ?? "—" }}</span>
                </template>
                <template #inviteeEmail-cell="{ row }">
                    <span class="text-slate-300">{{ row.original.inviteeEmail }}</span>
                </template>
                <template #message-cell="{ row }">
                    <span class="text-sm text-slate-400">{{ row.original.message || "—" }}</span>
                </template>
                <template #status-cell="{ row }">
                    <UBadge :color="statusColor(row.original.status)" variant="subtle">
                        {{ statusLabel(row.original.status) }}
                    </UBadge>
                </template>
                <template #action-cell="{ row }">
                    <UButton
                        icon="i-lucide-trash-2"
                        color="red"
                        variant="ghost"
                        size="xs"
                        :loading="deletingId === row.original.id"
                        @click="handleDelete(row.original.id)" />
                </template>
            </UTable>
        </UCard>

        <!-- 發送邀請 Modal -->
        <UModal v-model:open="openCreate" :ui="{ content: 'bg-[#363636]/80 backdrop-blur-sm border border-white/10 rounded-xl', overlay: 'bg-black/50' }">
            <template #header>
                <p class="font-semibold">發送筆記本邀請</p>
            </template>
            <template #body>
                <div class="space-y-4 p-1">
                    <UFormField label="受邀者 Email">
                        <UInput v-model="form.inviteeEmail" placeholder="user@example.com" />
                    </UFormField>
                    <UFormField label="筆記本">
                        <USelect
                            v-model="form.notebookId"
                            :items="notebookOptions"
                            placeholder="選擇筆記本" />
                    </UFormField>
                    <UFormField label="訊息（選填）">
                        <UTextarea v-model="form.message" placeholder="附帶訊息..." :rows="3" />
                    </UFormField>
                </div>
            </template>
            <template #footer>
                <div class="flex justify-end gap-2">
                    <UButton variant="ghost" color="neutral" @click="openCreate = false">取消</UButton>
                    <UButton
                        color="primary"
                        class="text-white"
                        :loading="creating"
                        :disabled="!form.inviteeEmail || !form.notebookId"
                        @click="handleCreate">
                        發送
                    </UButton>
                </div>
            </template>
        </UModal>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "#imports";
import { useInvitation, type InvitationDTO } from "~/composables/model/useInvitation";
import { useNotebook } from "~/composables/model/useNotebook";
import { useNotebookNav } from "~/composables/useNotebookNav";
import { useToast } from "#imports";

definePageMeta({ layout: "dashboard" });

const toast = useToast();
const { indexReceived, indexSent, createInvitation, updateInvitation, deleteInvitation } = useInvitation();
const { indexNotebook } = useNotebook();
const { fetchCoNotebooks } = useNotebookNav();

const received = ref<InvitationDTO[]>([]);
const sent = ref<InvitationDTO[]>([]);
const receivedLoading = ref(false);
const sentLoading = ref(false);
const actionId = ref<string | null>(null);
const deletingId = ref<string | null>(null);
const openCreate = ref(false);
const creating = ref(false);

const notebooks = ref<{ id: string; title: string }[]>([]);
const form = ref({ inviteeEmail: "", notebookId: "", message: "" });

const notebookOptions = computed(() =>
    notebooks.value.map((nb) => ({ label: nb.title, value: nb.id })),
);

const receivedColumns = [
    { accessorKey: "notebookTitle", header: "筆記本" },
    { accessorKey: "inviterName", header: "邀請人" },
    { accessorKey: "message", header: "訊息" },
    { accessorKey: "status", header: "狀態" },
    { id: "action", header: "" },
];

const sentColumns = [
    { accessorKey: "notebookTitle", header: "筆記本" },
    { accessorKey: "inviteeEmail", header: "受邀者" },
    { accessorKey: "message", header: "訊息" },
    { accessorKey: "status", header: "狀態" },
    { id: "action", header: "" },
];

async function loadReceived() {
    receivedLoading.value = true;
    try {
        const res = await indexReceived({ pageSize: 50 });
        received.value = res.items ?? [];
    } finally {
        receivedLoading.value = false;
    }
}

async function loadSent() {
    sentLoading.value = true;
    try {
        const res = await indexSent({ pageSize: 50 });
        sent.value = res.items ?? [];
    } finally {
        sentLoading.value = false;
    }
}

async function handleUpdate(id: string, action: "accept" | "reject") {
    actionId.value = id;
    try {
        await updateInvitation({ invitationId: id, status: action });
        toast.add({ title: action === "accept" ? "已接受邀請" : "已拒絕邀請", color: "success" });
        await loadReceived();
        if (action === "accept") await fetchCoNotebooks(true);
    } catch {
        toast.add({ title: "操作失敗，請稍後再試", color: "error" });
    } finally {
        actionId.value = null;
    }
}

async function handleDelete(id: string) {
    deletingId.value = id;
    try {
        await deleteInvitation(id);
        toast.add({ title: "邀請已刪除", color: "success" });
        await loadSent();
    } catch {
        toast.add({ title: "刪除失敗，請稍後再試", color: "error" });
    } finally {
        deletingId.value = null;
    }
}

async function handleCreate() {
    creating.value = true;
    try {
        await createInvitation({
            inviteeEmail: form.value.inviteeEmail,
            notebookId: form.value.notebookId,
            message: form.value.message || undefined,
        });
        toast.add({ title: "邀請已發送", color: "success" });
        openCreate.value = false;
        form.value = { inviteeEmail: "", notebookId: "", message: "" };
        await loadSent();
    } catch {
        toast.add({ title: "發送失敗，請確認 Email 是否存在", color: "error" });
    } finally {
        creating.value = false;
    }
}

function statusColor(status: string) {
    if (status === "accepted") return "green";
    if (status === "pending" || status === "resend") return "yellow";
    return "red";
}

function statusLabel(status: string) {
    const map: Record<string, string> = {
        pending: "待接受",
        accepted: "已接受",
        rejected: "已拒絕",
        resend: "重新邀請",
    };
    return map[status] ?? status;
}

onMounted(async () => {
    await Promise.all([
        loadReceived(),
        loadSent(),
        indexNotebook({ pageSize: 50 }).then((res) => {
            notebooks.value = res.items ?? [];
        }),
    ]);
});
</script>

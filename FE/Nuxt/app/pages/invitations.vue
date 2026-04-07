<template>
    <div class="p-6 space-y-6">
        <!-- Header -->
        <div>
            <p class="text-sm text-slate-400">{{ $t('pages.invitations.breadcrumb') }}</p>
            <h1 class="text-2xl font-semibold text-white">{{ $t('pages.invitations.title') }}</h1>
        </div>

        <!-- 收到的邀請 -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <template #header>
                <div class="flex items-center justify-between">
                    <p class="font-semibold">{{ $t('pages.invitations.received') }}</p>
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
                            {{ $t('common.accept') }}
                        </UButton>
                        <UButton
                            size="xs"
                            color="neutral"
                            variant="ghost"
                            class="border border-white/20"
                            :loading="actionId === row.original.id"
                            @click="handleUpdate(row.original.id, 'reject')">
                            {{ $t('common.reject') }}
                        </UButton>
                    </div>
                </template>
            </UTable>
        </UCard>

        <!-- 寄出的邀請 -->
        <UCard class="bg-slate-900/60 border-slate-800">
            <template #header>
                <div class="flex items-center justify-between">
                    <p class="font-semibold">{{ $t('pages.invitations.sent') }}</p>
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
                            {{ $t('pages.invitations.sendInvitation') }}
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
                <p class="font-semibold">{{ $t('pages.invitations.sendModal') }}</p>
            </template>
            <template #body>
                <div class="space-y-4 p-1">
                    <UFormField :label="$t('pages.invitations.inviteeEmail')">
                        <UInput v-model="form.inviteeEmail" placeholder="user@example.com" />
                    </UFormField>
                    <UFormField :label="$t('pages.invitations.notebook')">
                        <USelect
                            v-model="form.notebookId"
                            :items="notebookOptions"
                            :placeholder="$t('pages.invitations.notebookPlaceholder')" />
                    </UFormField>
                    <UFormField :label="$t('pages.invitations.message')">
                        <UTextarea v-model="form.message" :placeholder="$t('pages.invitations.messagePlaceholder')" :rows="3" />
                    </UFormField>
                </div>
            </template>
            <template #footer>
                <div class="flex justify-end gap-2">
                    <UButton variant="ghost" color="neutral" @click="openCreate = false">{{ $t('common.cancel') }}</UButton>
                    <UButton
                        color="primary"
                        class="text-white"
                        :loading="creating"
                        :disabled="!form.inviteeEmail || !form.notebookId"
                        @click="handleCreate">
                        {{ $t('common.send') }}
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

const { t } = useI18n();
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

const receivedColumns = computed(() => [
    { accessorKey: "notebookTitle", header: t('pages.invitations.colNotebook') },
    { accessorKey: "inviterName", header: t('pages.invitations.colInviter') },
    { accessorKey: "message", header: t('pages.invitations.colMessage') },
    { accessorKey: "status", header: t('pages.invitations.colStatus') },
    { id: "action", header: "" },
]);

const sentColumns = computed(() => [
    { accessorKey: "notebookTitle", header: t('pages.invitations.colNotebook') },
    { accessorKey: "inviteeEmail", header: t('pages.invitations.colInvitee') },
    { accessorKey: "message", header: t('pages.invitations.colMessage') },
    { accessorKey: "status", header: t('pages.invitations.colStatus') },
    { id: "action", header: "" },
]);

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
        toast.add({ title: action === "accept" ? t('pages.invitations.toastAccepted') : t('pages.invitations.toastRejected'), color: "success" });
        await loadReceived();
        if (action === "accept") await fetchCoNotebooks(true);
    } catch {
        toast.add({ title: t('pages.invitations.toastActionFailed'), color: "error" });
    } finally {
        actionId.value = null;
    }
}

async function handleDelete(id: string) {
    deletingId.value = id;
    try {
        await deleteInvitation(id);
        toast.add({ title: t('pages.invitations.toastDeleted'), color: "success" });
        await loadSent();
    } catch {
        toast.add({ title: t('pages.invitations.toastDeleteFailed'), color: "error" });
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
        toast.add({ title: t('pages.invitations.toastSent'), color: "success" });
        openCreate.value = false;
        form.value = { inviteeEmail: "", notebookId: "", message: "" };
        await loadSent();
    } catch {
        toast.add({ title: t('pages.invitations.toastSendFailed'), color: "error" });
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
        pending: t('pages.invitations.statusPending'),
        accepted: t('pages.invitations.statusAccepted'),
        rejected: t('pages.invitations.statusRejected'),
        resend: t('pages.invitations.statusResend'),
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

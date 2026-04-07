<template>
    <div class="space-y-4">
        <div class="border border-red-500/40 rounded-lg p-4 space-y-3 bg-red-950/20">
            <p class="text-sm font-medium text-red-400">
                {{ $t('settings.account.deleteTitle') }}
            </p>
            <p class="text-xs text-slate-400">
                {{ $t('settings.account.deleteWarning') }}
            </p>
            <UButton
                color="error"
                variant="soft"
                :loading="deleting"
                @click="handleDelete">
                {{ $t('settings.account.deleteConfirmBtn') }}
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
const { t } = useI18n();
const dialog = useDialogs();
const { signOut } = useAuth();
const deleting = ref(false);

function handleDelete() {
    dialog.confirm(
        t("settings.account.deleteWarning"),
        t("settings.account.deleteTitle"),
        async () => {
            deleting.value = true;
            try {
                await $fetch("/api/user", { method: "DELETE" });
                await signOut({ callbackUrl: "/signIn" });
            } finally {
                deleting.value = false;
            }
        },
    );
}
</script>

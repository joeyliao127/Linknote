<template>
    <div class="space-y-4">
        <UFormField :label="$t('settings.security.currentPassword')">
            <UInput
                v-model="form.oldPassword"
                type="password"
                class="w-full"
                autocomplete="current-password" />
        </UFormField>
        <UFormField :label="$t('settings.security.newPassword')">
            <UInput
                v-model="form.newPassword"
                type="password"
                class="w-full"
                autocomplete="new-password" />
        </UFormField>
        <UFormField :label="$t('settings.security.confirmPassword')">
            <UInput
                v-model="form.confirmPassword"
                type="password"
                class="w-full"
                autocomplete="new-password" />
            <p v-if="mismatch" class="text-xs text-red-400 mt-1">
                {{ $t('settings.security.passwordMismatch') }}
            </p>
        </UFormField>
        <div class="flex justify-end pt-2">
            <UButton :loading="saving" color="primary" :disabled="mismatch" @click="save">
                {{ $t('common.save') }}
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
const { t } = useI18n();
const toast = useToast();

const form = reactive({ oldPassword: "", newPassword: "", confirmPassword: "" });
const saving = ref(false);

const mismatch = computed(
    () => form.confirmPassword.length > 0 && form.newPassword !== form.confirmPassword,
);

async function save() {
    if (mismatch.value) return;
    saving.value = true;
    try {
        await $fetch("/api/user/password", {
            method: "PUT",
            body: { oldPassword: form.oldPassword, newPassword: form.newPassword },
        });
        toast.add({ title: t("settings.security.saveSuccess"), color: "primary" });
        form.oldPassword = "";
        form.newPassword = "";
        form.confirmPassword = "";
    } catch (err: any) {
        const msg = err?.data?.message?.includes("incorrect")
            ? t("settings.security.wrongPassword")
            : t("settings.security.saveFailed");
        toast.add({ title: msg, color: "alert" });
    } finally {
        saving.value = false;
    }
}
</script>

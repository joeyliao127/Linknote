<template>
    <div class="space-y-4">
        <UFormField :label="$t('settings.profile.username')">
            <UInput v-model="usernameInput" class="w-full" />
        </UFormField>
        <UFormField :label="$t('settings.profile.email')">
            <UInput :model-value="email" disabled class="w-full" />
            <p class="text-xs text-slate-500 mt-1">
                {{ $t('settings.profile.emailReadonly') }}
            </p>
        </UFormField>
        <div class="flex justify-end pt-2">
            <UButton :loading="saving" color="primary" @click="save">
                {{ $t('common.save') }}
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
const props = defineProps<{
    username: string;
    email: string;
}>();

const emit = defineEmits<{
    (e: "saved", username: string): void;
}>();

const { t } = useI18n();
const toast = useToast();

const usernameInput = ref(props.username);
const saving = ref(false);

watch(() => props.username, (val) => { usernameInput.value = val; });

async function save() {
    saving.value = true;
    try {
        await $fetch("/api/user/profile", {
            method: "PUT",
            body: { username: usernameInput.value },
        });
        toast.add({ title: t("settings.profile.saveSuccess"), color: "primary" });
        emit("saved", usernameInput.value);
    } catch {
        toast.add({ title: t("settings.profile.saveFailed"), color: "alert" });
    } finally {
        saving.value = false;
    }
}
</script>

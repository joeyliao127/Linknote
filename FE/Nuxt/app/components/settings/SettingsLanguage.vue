<template>
    <div class="space-y-3">
        <p class="text-sm text-slate-400">{{ $t('settings.language.description') }}</p>
        <div class="flex gap-3">
            <UButton
                v-for="locale in locales"
                :key="locale.code"
                :variant="currentLocale === locale.code ? 'solid' : 'outline'"
                :color="currentLocale === locale.code ? 'primary' : 'neutral'"
                @click="setLanguage(locale.code)">
                {{ locale.name }}
            </UButton>
        </div>
    </div>
</template>

<script setup lang="ts">
const { locale, setLocale } = useI18n();

const locales = [
    { code: "zh", name: "繁體中文" },
    { code: "en", name: "English" },
];

const currentLocale = computed(() => locale.value);

function setLanguage(code: string) {
    setLocale(code as "zh" | "en");
    if (import.meta.client) {
        localStorage.setItem("locale", code);
    }
}
</script>

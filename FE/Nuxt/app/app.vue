<template>
    <UApp>
        <NuxtLayout>
            <NuxtPage />
            <DialogsContainer />
        </NuxtLayout>
    </UApp>
</template>

<script setup lang="ts">
import { useAppConfig, useRuntimeConfig } from "nuxt/app";
import DialogsContainer from "./components/dialog/DialogsContainer.vue";

const runtimeConfig = useRuntimeConfig();
const appConfig = useAppConfig();
</script>

<template>
    <div class="page">
        <slot />
    </div>
</template>
<script setup lang="ts">
import dayjs from "dayjs";
import { useAppConfig } from "#app";
const appConfig = useAppConfig();
</script>
<style></style>

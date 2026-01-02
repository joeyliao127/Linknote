<template>
    <div class="min-h-screen w-full bg-slate-950 text-slate-100 overflow-hidden">
        <slot />
    </div>
</template>
<script setup lang="ts">
import { onMounted, provide, useRouter } from "#imports";
import { useNotebookNav } from "~/composables/useNotebookNav";

const { fetchNotebooks } = useNotebookNav();
const router = useRouter();

const dashboardNav = {
    goNotebooks: () => router.push("/notebooks"),
    goCoNotebooks: () => router.push({ path: "/notebooks", query: { collab: "true" } }),
};

provide("dashboard-nav", dashboardNav);

onMounted(() => {
    fetchNotebooks();
});
</script>

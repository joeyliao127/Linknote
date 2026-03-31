<template>
    <div class="dashboard-container">
        <div class="dashboard-bg" />
        <div class="dashboard-layout">
            <aside class="dashboard-sidebar">
                <SidebarV2 @logout="handleLogout" />
            </aside>
            <main class="dashboard-main">
                <slot />
            </main>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, useRouter } from "#imports";
import { useNotebookNav } from "~/composables/useNotebookNav";
import SidebarV2 from "~/components/dashboard/SidebarV2.vue";

const { fetchNotebooks } = useNotebookNav();
const router = useRouter();

function handleLogout() {
    router.push("/signIn");
}

onMounted(() => {
    fetchNotebooks();
});
</script>

<style scoped>
.dashboard-container {
    position: relative;
    width: 100%;
    height: 100vh;
    overflow: hidden;
}

.dashboard-bg {
    position: absolute;
    inset: 0;
    background-image: url("/image/fugi.jpeg");
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    z-index: 0;
}

.dashboard-layout {
    position: relative;
    z-index: 1;
    display: flex;
    width: 100%;
    height: 100%;
}

.dashboard-sidebar {
    width: 16rem;
    height: 100%;
    flex-shrink: 0;
    background: rgba(31, 34, 31, 0.75);
    backdrop-filter: blur(2px);
    border-right: 1px solid rgba(255, 255, 255, 0.08);
    overflow-y: auto;
}

.dashboard-main {
    flex: 1;
    height: 100%;
    background: rgba(31, 34, 31, 0.5);
    backdrop-filter: blur(0px);
    overflow-y: auto;
    padding: 0;
    color: rgba(226, 232, 240, 0.9);
}
</style>

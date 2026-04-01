<template>
    <div class="h-screen w-screen flex flex-col overflow-hidden bg-[url('/image/fugi.jpeg')] bg-cover bg-center bg-fixed">
        <slot name="sidebar" />
        <header class="h-12 shrink-0">
            <slot name="header" />
        </header>
        <main class="flex-1 min-h-0 overflow-hidden">
            <slot name="main" />
        </main>
    </div>
</template>
<script setup></script>

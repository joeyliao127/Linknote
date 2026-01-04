<template>
    <div>
        <div>
            <aside>
                <slot name="sidebar" />
            </aside>
        </div>
        <div class="h-10">
            <header>
                <slot name="header" />
            </header>
        </div>
        <div class="h-[calc(100vh-40px)] bg-background">
            <main>
                <slot name="main" />
            </main>
        </div>
    </div>
</template>
<script setup></script>

<style></style>

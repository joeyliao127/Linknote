<!-- 
1. dashboard layout 只用於套用 DashboardGroup，不指定 sidebar, main, search 的實作
2. 因為 notes, note 的 sidebar 不同，search 目標也不同

-->
<template>
    <UDashboardGroup>
        <slot>
            <slot name="sidebar" />
            <slot name="main" />
            <slot name="search" />
        </slot>
    </UDashboardGroup>
</template>

<script setup></script>

<style></style>

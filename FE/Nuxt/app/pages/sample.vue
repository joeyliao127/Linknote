<template>
    <div class="p-8 space-y-6">
        <h1 class="text-3xl font-bold text-primary">Sample Color Test</h1>
        <UBlogPost title="Introducing Nuxt Icon v1" />
        <div class="space-x-4 flex flex-col gap-4">
            <h2 class="title text-2xl">Buttons</h2>
            <div class="flex gap-2">
                <UButton
                    class="bg-primary px-4 py-2 rounded hover:bg-primary/90">
                    Primary
                </UButton>
                <UButton class="bg-accent px-4 py-2 rounded hover:bg-accent/90">
                    Accent
                </UButton>

                <UFieldGroup>
                    <UTooltip text="TODO">
                        <UButton variant="outline" icon="uil-mail" />
                    </UTooltip>
                    <UButton variant="outline" icon="uil-github" />
                </UFieldGroup>
            </div>
            <h3 class="title text-xl">Button with icon</h3>
            <div class="flex gap-2"></div>
            <h3 class="title text-xl">Variants</h3>
            <div class="flex gap-2">
                <UButton variant="solid" label="sloid" />
                <UButton variant="outline" label="outline" />
                <UButton variant="subtle" label="subtle" />
                <UButton variant="soft" label="soft" />
                <UButton variant="ghost" label="ghost" />
                <UButton variant="link" label="link" />
                <UButton variant="solid" label="sloid" color="secondary" />
                <UButton variant="outline" label="outline" color="accent" />
                <UButton variant="subtle" label="subtle" color="accent" />
                <UButton variant="soft" label="soft" color="accent" />
                <UButton variant="ghost" label="ghost" color="accent" />
                <UButton variant="link" label="link" color="accent" />
            </div>
        </div>
        <UCarousel></UCarousel>
        <USeparator>
            <h2 class="title text-2xl">Charts and Cards</h2>
        </USeparator>
        <div class="grid grid-cols-4 gap-6">
            <UCard class="h-50"></UCard>
            <UCard class="h-50"></UCard>
            <UCard class="h-50"></UCard>
            <UCard class="h-50">
                <Bar :data="chartData" :options="chartOptions" />
            </UCard>

            <UCard class="h-50 col-span-4">
                <Bar :data="chartData" :options="chartOptions" />
            </UCard>
        </div>

        <USeparator>
            <h2>Avatar</h2>
        </USeparator>
        <div class="px-4">
            <UAvatar src="https://github.com/benjamincanac.png" />
            <UAvatarGroup>
                <UAvatar src="https://github.com/benjamincanac.png" />
                <UAvatar src="https://github.com/benjamincanac.png" />
                <UAvatar src="https://github.com/benjamincanac.png" />
            </UAvatarGroup>
        </div>

        <USeparator>
            <h2 class="title text-2xl">Sigin Form</h2>
        </USeparator>

        <FormSignTabs />
    </div>
</template>
<script setup lang="ts">
import { Bar } from "vue-chartjs";
import { ref } from "vue";

const chartData = ref({
    labels: [
        "圖表一",
        "圖表二",
        "圖表三",
        "圖表四",
        "圖表五",
        "圖表六",
        "圖表七",
    ],
    datasets: [
        {
            label: "測試資料",
            data: [65, 59, 80, 81, 56, 55, 40],
            backgroundColor: [
                "rgba(255, 99, 132, 0.2)",
                "rgba(255, 159, 64, 0.2)",
                "rgba(255, 205, 86, 0.2)",
                "rgba(75, 192, 192, 0.2)",
                "rgba(54, 162, 235, 0.2)",
                "rgba(153, 102, 255, 0.2)",
                "rgba(201, 203, 207, 0.2)",
            ],
            borderColor: [
                "rgb(255, 99, 132)",
                "rgb(255, 159, 64)",
                "rgb(255, 205, 86)",
                "rgb(75, 192, 192)",
                "rgb(54, 162, 235)",
                "rgb(153, 102, 255)",
                "rgb(201, 203, 207)",
            ],
            borderWidth: 1,
        },
    ],
});

const chartOptions = ref({
    //表示圖表會 自動根據容器大小調整尺寸
    responsive: true,
    // 是否要維持原生的 長寬比例（aspect ratio）
    maintainAspectRatio: false,
    scales: {
        // 控制 y 軸的起始值是否從 0 開始
        y: { beginAtZero: true },
    },
    plugins: {
        //隱藏圖表右上角的「圖例（legend）」
        legend: { display: false },
    },
});
</script>

<style>
@reference '~/assets/css/tailwind.css';

.title {
    @apply text-white pb-4 font-medium;
}
</style>

<template>
    <div class="flex flex-col h-full p-6 gap-6">
        <!-- Header -->
        <div class="flex items-center justify-between">
            <div class="flex flex-col gap-1">
                <p class="text-sm text-slate-200/50">筆記本</p>
                <h1 class="text-3xl font-semibold text-white leading-tight">
                    建立新的筆記本
                </h1>
            </div>
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-arrow-left"
                class="border border-white/20 text-white hover:border-white/40 hover:bg-white/5"
                @click="goBack">
                返回
            </UButton>
        </div>

        <!-- Form Area -->
        <div class="flex flex-1 items-start justify-center pt-8">
            <div class="w-[420px] aspect-[4/5] flex flex-col">
                <!-- Card -->
                <div
                    class="flex flex-col h-full bg-white/[0.04] border border-white/10 rounded-lg p-8 gap-6">
                    <!-- Card Header -->
                    <div
                        class="flex items-center gap-3 pb-4 border-b border-white/10 shrink-0">
                        <div
                            class="flex items-center justify-center w-9 h-9 rounded bg-primary/30">
                            <UIcon
                                name="i-lucide-notebook-pen"
                                class="w-5 h-5 text-primary-light" />
                        </div>
                        <p class="font-semibold text-white">填寫筆記本資訊</p>
                    </div>

                    <!-- Form -->
                    <NotebookForm
                        :submitting="submitting"
                        submit-label="建立"
                        class="flex-1 min-h-0"
                        @submit="handleSubmit" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRouter, useAuth, useToast } from "#imports";
import NotebookForm from "~/components/notebook/NotebookForm.vue";
import { useNotebook } from "~/composables/model/useNotebook";
import { useNotebookNav } from "~/composables/useNotebookNav";

definePageMeta({ layout: "dashboard" });

const router = useRouter();
const toast = useToast();
const auth = useAuth();
const { createNotebook } = useNotebook();
const { fetchNotebooks } = useNotebookNav();

const submitting = ref(false);
const userId = computed(() => auth.data.value?.user?.id || "");

async function handleSubmit(value: {
    title: string;
    description?: string;
    active?: boolean;
}) {
    if (submitting.value) return;
    submitting.value = true;
    try {
        await createNotebook(userId.value, {
            title: value.title,
            description: value.description,
            active: value.active,
        });
        toast.add({ title: "建立成功", color: "accent" });
        fetchNotebooks(true);
        router.push("/notebooks");
    } catch (error: any) {
        toast.add({
            title: "建立失敗",
            description: error?.data?.message || error?.message || "請稍後再試",
            color: "red",
        });
    } finally {
        submitting.value = false;
    }
}

function goBack() {
    router.back();
}
</script>

<template>
    <UFormField :label="label" :name="name" :required="required">
        <UInput
            class="w-full"
            :type="type"
            :trailing-icon="getIcon"
            :placeholder="placeholder" />
    </UFormField>
</template>

<script setup lang="ts">
import { computed } from "vue";

const { label, name, type, required, icon, placeholder } = defineProps({
    label: {
        type: String,
        required: false,
    },
    name: {
        type: String,
        required: false,
    },

    type: {
        type: String,
        required: false,
    },

    required: {
        type: Boolean,
        required: false,
        default: false,
    },

    icon: {
        type: String,
        required: false,
        default: "",
    },

    placeholder: {
        type: String,
        required: false,
        default: "",
    },
});

const getIcon = computed(() => {
    if (icon) return `i-lucide-${icon}`;
    else return "";
});
</script>

<style></style>

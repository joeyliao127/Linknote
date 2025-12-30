<template>
    <div
        class="px-2 py-1 flex justify-between items-center hover:border"
        :class="getBgStyle">
        <div class="cursor-pointer w-[80%] truncate" @click="selectItem">
            <p class="w-full truncate">{{ props.title }}</p>
        </div>
        <UIcon
            :name="getStar"
            class="w-5 text-yellow-400 cursor-pointer"
            @click="toggleStar" />
    </div>
</template>
<script setup>
const props = defineProps({
    id: {
        type: String,
        required: true,
    },
    title: {
        type: String,
        required: true,
    },
    active: {
        type: Boolean,
        default: false,
    },

    stared: {
        type: Boolean,
        default: false,
    },
});

const emits = defineEmits(["select_item", "toggle_star"]);

const getBgStyle = computed(() => {
    return props.active ? "bg-gray-500" : "";
});

const getStar = computed(() => {
    return props.stared ? "uis-star" : "uil-star";
});

function selectItem() {
    emits("select_item", props.id);
}

function toggleStar() {
    emits("toggle_star", props.id);
}
</script>

<style></style>

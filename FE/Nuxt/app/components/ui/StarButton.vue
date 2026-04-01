<template>
    <div>
        <template v-if="props.stared">
            <UButton variant="ghost" icon="uis-star" class="text-yellow-400 hover:text-yellow-300" @click="change" />
        </template>

        <template v-else>
            <UButton
                variant="ghost"
                icon="uil-star"
                @click="change"
                color="slate-800"
                class="hover:text-amber-300" />
        </template>
    </div>
</template>
<script setup lang="ts">
interface Props {
    id: string;
    stared: boolean;
    size?: string;
}

const props = withDefaults(defineProps<Props>(), {
    stared: false,
    size: "md",
});

const emits = defineEmits(["change"]);

function change() {
    emits("change", { id: props.id, stared: !props.stared });
}
</script>

<style></style>

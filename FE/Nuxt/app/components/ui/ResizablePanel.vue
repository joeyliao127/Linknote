<template>
    <div
        ref="panelRef"
        class="relative"
        :style="{
            width: orientation === 'vertical' ? `${currentSize}px` : '100%',
            height: orientation === 'horizontal' ? `${currentSize}px` : '100%',
            minWidth: orientation === 'vertical' ? `${minSize}px` : undefined,
            maxWidth: orientation === 'vertical' ? `${maxSize}px` : undefined,
            minHeight:
                orientation === 'horizontal' ? `${minSize}px` : undefined,
            maxHeight:
                orientation === 'horizontal' ? `${maxSize}px` : undefined,
        }">
        <slot />

        <!-- Resize Handle -->
        <div
            v-if="resizable"
            :class="[
                'absolute z-10 transition-colors hover:bg-gray-500',
                orientation === 'vertical'
                    ? 'right-0 top-0 h-full w-1 cursor-col-resize'
                    : 'bottom-0 left-0 w-full h-1 cursor-row-resize',
            ]"
            @mousedown="startResize" />
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from "vue";

interface Props {
    orientation?: "vertical" | "horizontal";
    defaultSize?: number;
    minSize?: number;
    maxSize?: number;
    resizable?: boolean;
    storageKey?: string;
}

const props = withDefaults(defineProps<Props>(), {
    orientation: "vertical",
    defaultSize: 400,
    minSize: 200,
    maxSize: 1200,
    resizable: true,
});

const emit = defineEmits<{
    resize: [size: number];
}>();

const panelRef = ref<HTMLElement | null>(null);
const currentSize = ref(props.defaultSize);
const isResizing = ref(false);
const startPos = ref(0);
const startSize = ref(0);

// Load saved size from localStorage
onMounted(() => {
    if (props.storageKey) {
        const savedSize = localStorage.getItem(props.storageKey);
        if (savedSize) {
            const size = parseInt(savedSize, 10);
            if (size >= props.minSize && size <= props.maxSize) {
                currentSize.value = size;
            }
        }
    }
});

const startResize = (e: MouseEvent) => {
    isResizing.value = true;
    startPos.value = props.orientation === "vertical" ? e.clientX : e.clientY;
    startSize.value = currentSize.value;

    document.addEventListener("mousemove", handleResize);
    document.addEventListener("mouseup", stopResize);

    // Prevent text selection during resize
    e.preventDefault();
};

const handleResize = (e: MouseEvent) => {
    if (!isResizing.value) return;

    const currentPos = props.orientation === "vertical" ? e.clientX : e.clientY;
    const delta = currentPos - startPos.value;
    let newSize = startSize.value + delta;

    // Clamp size between min and max
    newSize = Math.max(props.minSize, Math.min(props.maxSize, newSize));

    currentSize.value = newSize;
    emit("resize", newSize);
};

const stopResize = () => {
    if (isResizing.value) {
        isResizing.value = false;

        // Save to localStorage
        if (props.storageKey) {
            localStorage.setItem(
                props.storageKey,
                currentSize.value.toString()
            );
        }
    }

    document.removeEventListener("mousemove", handleResize);
    document.removeEventListener("mouseup", stopResize);
};

onBeforeUnmount(() => {
    document.removeEventListener("mousemove", handleResize);
    document.removeEventListener("mouseup", stopResize);
});

// Watch for external size changes
watch(
    () => props.defaultSize,
    (newSize) => {
        if (!props.storageKey) {
            currentSize.value = newSize;
        }
    }
);
</script>

<template>
    <UCard
        class="h-full bg-slate-900/60 border-slate-800 hover:border-accent/60 transition-colors cursor-default">
        <div class="flex items-start justify-between gap-2">
            <div class="flex flex-col gap-1 min-w-0 flex-1">
                <div class="flex items-center gap-2">
                    <p class="font-semibold text-lg truncate">
                        {{ title }}
                    </p>
                    <UBadge
                        v-if="collab"
                        color="primary"
                        variant="soft"
                        size="xs">
                        共編
                    </UBadge>
                </div>
                <div
                    v-if="!isEditingDesc"
                    class="desc-display"
                    @click.stop="startEditingDesc">
                    <p class="text-sm text-slate-400 line-clamp-2">
                        {{ description || "尚未填寫描述" }}
                    </p>
                </div>
                <div
                    v-else
                    class="desc-edit-container"
                    @click.stop>
                    <UInput
                        v-model="editingDesc"
                        placeholder="輸入描述..."
                        class="desc-input"
                        @keyup.enter="saveDescription"
                        @keyup.escape="cancelEdit" />
                    <div class="desc-edit-actions">
                        <UButton
                            size="xs"
                            variant="soft"
                            color="green"
                            @click="saveDescription">
                            儲存
                        </UButton>
                        <UButton
                            size="xs"
                            variant="ghost"
                            @click="cancelEdit">
                            取消
                        </UButton>
                    </div>
                </div>
            </div>
            <slot name="actions">
                <UButton
                    icon="i-lucide-arrow-up-right"
                    variant="ghost"
                    size="xs"
                    @click.stop="$emit('open', id)" />
            </slot>
        </div>

        <div
            class="mt-3 flex flex-wrap items-center gap-4 text-xs text-slate-400">
            <div class="flex items-center gap-1">
                <UIcon name="i-lucide-notebook-pen" class="w-4 h-4" />
                <span>{{ noteCount ?? 0 }} 筆記</span>
            </div>
            <div v-if="owner" class="flex items-center gap-1">
                <UIcon name="i-lucide-user" class="w-4 h-4" />
                <span class="truncate">{{ owner }}</span>
            </div>
            <div v-if="updatedAt" class="flex items-center gap-1">
                <UIcon name="i-lucide-clock-3" class="w-4 h-4" />
                <span>{{ formattedUpdatedAt }}</span>
            </div>
        </div>

        <div class="mt-4 flex items-center justify-between gap-3">
            <UButton
                v-if="deletable"
                color="red"
                variant="ghost"
                size="sm"
                icon="i-lucide-trash-2"
                class="justify-start"
                @click.stop="$emit('delete', id)">
                刪除
            </UButton>

            <div class="flex-1" />

            <UButton
                variant="soft"
                color="accent"
                size="sm"
                icon-right="i-lucide-arrow-right"
                @click="$emit('open', id)">
                查看
            </UButton>
        </div>
    </UCard>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

const props = withDefaults(
    defineProps<{
        id: string;
        title: string;
        description?: string;
        noteCount?: number;
        updatedAt?: string | Date;
        owner?: string;
        collab?: boolean;
        deletable?: boolean;
    }>(),
    {
        description: "",
        noteCount: 0,
        updatedAt: undefined,
        owner: "",
        collab: false,
        deletable: true,
    }
);

const emit = defineEmits<{
    (e: "open", id: string): void;
    (e: "delete", id: string): void;
    (e: "update-description", id: string, description: string): void;
}>();

const isEditingDesc = ref(false);
const editingDesc = ref(props.description);

const formattedUpdatedAt = computed(() => {
    if (!props.updatedAt) return "";
    const date =
        props.updatedAt instanceof Date
            ? props.updatedAt
            : new Date(props.updatedAt);
    return date.toLocaleDateString("zh-TW", {
        month: "short",
        day: "2-digit",
    });
});

function startEditingDesc() {
    editingDesc.value = props.description;
    isEditingDesc.value = true;
}

async function saveDescription() {
    if (editingDesc.value === props.description) {
        isEditingDesc.value = false;
        return;
    }
    emit("update-description", props.id, editingDesc.value);
    isEditingDesc.value = false;
}

function cancelEdit() {
    isEditingDesc.value = false;
}
</script>

<style scoped>
.desc-display {
    padding: 0.25rem 0;
    border-radius: 3px;
    transition: background-color 150ms ease;
    cursor: pointer;
}

.desc-display:hover {
    background-color: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(74, 222, 128, 0.3);
}

.desc-edit-container {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.desc-input {
    width: 100%;
}

.desc-input :deep(input) {
    background: transparent;
    border: 1px solid rgba(74, 222, 128, 0.5);
    border-radius: 3px;
    color: rgba(248, 250, 252, 0.9);
    padding: 0.4rem 0.6rem;
    font-size: 0.875rem;
    transition: border-color 150ms ease;
}

.desc-input :deep(input::placeholder) {
    color: rgba(226, 232, 240, 0.4);
}

.desc-input :deep(input:focus) {
    outline: none;
    border-color: rgba(74, 222, 128, 0.8);
}

.desc-edit-actions {
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end;
}
</style>

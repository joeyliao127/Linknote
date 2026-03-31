<template>
    <div class="w-64 p-4 space-y-3">
        <!-- Title -->
        <h3 class="text-sm font-semibold text-white">Tags</h3>

        <!-- Tag List -->
        <div class="space-y-1 max-h-48 overflow-y-auto">
            <template v-for="tag in tags" :key="tag.id">
                <div
                    class="flex items-center gap-2 px-2 py-1.5 rounded hover:bg-white/10 transition-colors cursor-pointer group"
                    @click="toggleTag(tag.id)">
                    <!-- Checkmark Icon -->
                    <div class="w-4 h-4 flex items-center justify-center flex-shrink-0">
                        <UIcon
                            v-if="isTagSelected(tag.id)"
                            name="i-lucide-check"
                            class="w-4 h-4 text-accent" />
                    </div>

                    <!-- Tag Name -->
                    <span class="text-sm text-slate-300 flex-1">{{ tag.title }}</span>

                    <!-- Delete Icon -->
                    <button
                        class="p-1 rounded hover:bg-slate-600 opacity-0 group-hover:opacity-100 transition-opacity"
                        @click.stop="$emit('delete', tag.id)">
                        <UIcon name="i-lucide-trash-2" class="w-3.5 h-3.5 text-slate-400" />
                    </button>
                </div>
            </template>

            <p v-if="tags.length === 0" class="text-xs text-slate-500 italic py-2">
                尚無標籤
            </p>
        </div>

        <!-- Divider -->
        <div v-if="tags.length > 0" class="border-t border-slate-700/50"></div>

        <!-- Create New Tag -->
        <div class="flex gap-2">
            <UInput
                v-model="newTagName"
                placeholder="新增標籤"
                size="sm"
                @keyup.enter="handleCreateTag" />
            <UButton
                icon="i-lucide-plus"
                size="sm"
                variant="ghost"
                @click="handleCreateTag" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import type { Tag } from "~~/types/Tag";

const props = defineProps<{
    tags: Tag[];
    selectedTagIds: string[];
}>();

const emit = defineEmits<{
    (e: "update:selected", value: string[]): void;
    (e: "create", name: string): void;
    (e: "delete", tagId: string): void;
}>();

const newTagName = ref("");

function isTagSelected(tagId: string): boolean {
    return props.selectedTagIds.includes(tagId);
}

function toggleTag(tagId: string) {
    const updated = isTagSelected(tagId)
        ? props.selectedTagIds.filter((id) => id !== tagId)
        : [...props.selectedTagIds, tagId];
    emit("update:selected", updated);
}

function handleCreateTag() {
    if (newTagName.value.trim()) {
        emit("create", newTagName.value.trim());
        newTagName.value = "";
    }
}
</script>

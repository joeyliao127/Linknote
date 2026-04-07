<template>
    <UCard class="h-full border-none transition-colors cursor-default">
        <div class="flex items-start justify-between gap-2">
            <div class="flex flex-col gap-1 min-w-0 flex-1">
                <div class="flex items-center gap-2">
                    <p class="font-semibold text-lg truncate">
                        {{ title }}
                    </p>
                    <UBadge
                        v-if="collab"
                        color="accent"
                        variant="soft"
                        size="xs">
                        {{ $t('components.notebookCard.collab') }}
                    </UBadge>
                </div>
                <div
                    v-if="!isEditingDesc"
                    class="py-1 pl-2 rounded cursor-pointer transition-colors border border-transparent hover:bg-white/5 hover:border-accent/30"
                    @click.stop="startEditingDesc">
                    <p class="text-sm text-slate-400 line-clamp-2">
                        {{ description || $t('components.notebookCard.noDescription') }}
                    </p>
                </div>
                <div v-else class="flex flex-col gap-2" @click.stop>
                    <UInput
                        v-model="editingDesc"
                        :placeholder="$t('components.notebookCard.descPlaceholder')"
                        class="w-full"
                        :ui="{
                            base: 'bg-transparent border border-accent rounded text-slate-50/90 text-sm placeholder:text-slate-200/40 transition-colors',
                        }"
                        @keyup.enter="saveDescription"
                        @keyup.escape="cancelEdit" />
                    <div class="flex gap-2 justify-end">
                        <UButton
                            size="xs"
                            variant="soft"
                            color="accent"
                            @click="saveDescription">
                            {{ $t('common.save') }}
                        </UButton>
                        <UButton
                            size="xs"
                            variant="ghost"
                            color="secondary"
                            @click="cancelEdit">
                            {{ $t('common.cancel') }}
                        </UButton>
                    </div>
                </div>
            </div>
            <slot name="actions">
                <UButton
                    icon="i-lucide-arrow-up-right"
                    variant="ghost"
                    color="accent"
                    size="xs"
                    @click.stop="$emit('open', id)" />
            </slot>
        </div>

        <div class="mt-4 flex items-center justify-between gap-3">
            <UButton
                v-if="deletable"
                color="secondary"
                variant="ghost"
                size="sm"
                icon="i-lucide-trash-2"
                class="relative -bottom-1 -left-3"
                @click.stop="confirmDelete">
                {{ $t('common.delete') }}
            </UButton>

            <div class="flex-1" />

            <div
                class="mt-3 flex flex-wrap items-center gap-4 text-xs text-slate-400">
                <div class="flex items-center gap-1">
                    <UIcon name="i-lucide-notebook-pen" class="w-4 h-4" />
                    <span>{{ $t('components.notebookCard.noteCount', { count: noteCount ?? 0 }) }}</span>
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
        </div>
    </UCard>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useDialogs } from "~/composables/useDialogs";

const { t } = useI18n();

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
    },
);

const emit = defineEmits<{
    (e: "open", id: string): void;
    (e: "delete", id: string): void;
    (e: "update-description", id: string, description: string): void;
}>();

const { confirm } = useDialogs();

function confirmDelete() {
    confirm(
        t('components.notebookCard.deleteConfirm', { title: props.title }),
        undefined,
        () => emit("delete", props.id),
    );
}

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

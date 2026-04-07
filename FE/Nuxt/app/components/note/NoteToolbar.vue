<template>
    <div class="inline-flex items-center gap-2">
        <!-- New Note Button -->
        <UTooltip :text="$t('components.noteToolbar.createNote')">
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-plus"
                class="border border-white/20 text-white hover:border-white/40 hover:bg-white/5"
                :loading="creating"
                @click="$emit('create')" />
        </UTooltip>

        <!-- Reset Filter -->
        <UTooltip :text="$t('components.noteToolbar.clearFilters')">
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-wand-2"
                class="border border-white/20 text-white hover:border-white/40 hover:bg-white/5"
                @click="$emit('reset')" />
        </UTooltip>

        <!-- Tag Filter (Popover) -->
        <UPopover :popper="{ placement: 'bottom' }">
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-tag"
                :class="
                    selectedTagsModel.length > 0
                        ? 'border border-accent bg-accent/15 text-accent hover:bg-accent/25'
                        : 'border border-white/20 text-white hover:border-white/40 hover:bg-white/5'
                " />

            <template #content>
                <TagForm
                    :tags="tags"
                    :selected-tag-ids="selectedTagsModel"
                    @update:selected="
                        $emit('change-tag', $event?.length ? $event : null)
                    "
                    @create="$emit('create-tag', $event)"
                    @delete="$emit('delete-tag', $event)" />
            </template>
        </UPopover>

        <!-- Star Filter -->
        <UTooltip :text="$t('components.noteToolbar.starOnly')">
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-star"
                :class="
                    starOnly
                        ? 'border border-accent bg-accent/15 text-accent hover:bg-accent/25'
                        : 'border border-white/20 text-white hover:border-white/40 hover:bg-white/5'
                "
                @click="$emit('toggle-star', !starOnly)" />
        </UTooltip>

        <!-- Sort -->
        <UTooltip :text="$t('components.noteToolbar.sortByDate')">
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-arrow-up-down"
                :class="
                    sortOrder === 'desc'
                        ? 'border border-accent bg-accent/15 text-accent hover:bg-accent/25'
                        : 'border border-white/20 text-white hover:border-white/40 hover:bg-white/5'
                "
                @click="
                    $emit('change-sort', sortOrder === 'asc' ? 'desc' : 'asc')
                " />
        </UTooltip>

        <!-- Search -->
        <UInput
            v-model="keywordDraft"
            icon="i-lucide-search"
            :placeholder="$t('components.noteToolbar.keyword')"
            size="sm"
            color="neutral"
            variant="outline"
            class="w-40"
            @keyup.enter="emitSearch" />

        <!-- Delete Notebook -->
        <UTooltip :text="$t('components.noteToolbar.deleteNotebook')">
            <UButton
                variant="ghost"
                color="neutral"
                icon="i-lucide-trash-2"
                class="border border-white/20 text-white hover:border-white/40 hover:bg-white/5"
                :loading="deletingNotebook"
                @click="confirmDeleteNotebook" />
        </UTooltip>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import TagForm from "~/components/note/TagForm.vue";
import type { Tag } from "~~/types/Tag";
import { useDialogs } from "~/composables/useDialogs";

const { t } = useI18n();

const props = withDefaults(
    defineProps<{
        tags?: Tag[];
        selectedTags?: string[] | null;
        selectedTagIds?: string[];
        keyword?: string;
        starOnly?: boolean;
        sortOrder?: "asc" | "desc";
        creating?: boolean;
        deletingNotebook?: boolean;
    }>(),
    {
        tags: () => [],
        selectedTags: () => [],
        selectedTagIds: () => [],
        keyword: "",
        starOnly: false,
        sortOrder: "desc",
        creating: false,
        deletingNotebook: false,
    },
);

const emit = defineEmits<{
    (e: "create"): void;
    (e: "reset"): void;
    (e: "toggle-star", value: boolean): void;
    (e: "change-tag", value: string[] | null): void;
    (e: "change-sort", value: "asc" | "desc"): void;
    (e: "search", value: string): void;
    (e: "delete-notebook"): void;
    (e: "create-tag", name: string): void;
    (e: "delete-tag", tagId: string): void;
}>();

const selectedTagsModel = computed<string[]>({
    get: () => props.selectedTags ?? props.selectedTagIds ?? [],
    set: (value) => emit("change-tag", value?.length ? value : null),
});

const { confirm } = useDialogs();

function confirmDeleteNotebook() {
    confirm(
        t('components.noteToolbar.deleteNotebookConfirm'),
        undefined,
        () => emit("delete-notebook"),
    );
}

const keywordDraft = ref(props.keyword ?? "");
watch(
    () => props.keyword,
    (v) => {
        keywordDraft.value = v ?? "";
    },
);

function emitSearch() {
    emit("search", keywordDraft.value);
}
</script>

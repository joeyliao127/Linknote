<template>
    <div class="flex flex-col h-full overflow-hidden">
        <!-- Toolbar -->
        <div
            class="flex flex-wrap items-center gap-0.5 px-2 py-1 border-b border-slate-700/60 bg-black/50 backdrop-blur-sm shrink-0">
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('bold')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                @click="editor?.chain().focus().toggleBold().run()">
                <span class="font-bold text-xs">B</span>
            </UButton>
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('italic')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                @click="editor?.chain().focus().toggleItalic().run()">
                <span class="italic text-xs">I</span>
            </UButton>
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('strike')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                @click="editor?.chain().focus().toggleStrike().run()">
                <span class="line-through text-xs">S</span>
            </UButton>

            <div class="w-px h-4 bg-slate-700 mx-1 shrink-0" />

            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('heading', { level: 1 })
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                @click="
                    editor?.chain().focus().toggleHeading({ level: 1 }).run()
                ">
                <span class="text-xs font-bold">H1</span>
            </UButton>
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('heading', { level: 2 })
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                @click="
                    editor?.chain().focus().toggleHeading({ level: 2 }).run()
                ">
                <span class="text-xs font-bold">H2</span>
            </UButton>
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('heading', { level: 3 })
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                @click="
                    editor?.chain().focus().toggleHeading({ level: 3 }).run()
                ">
                <span class="text-xs font-bold">H3</span>
            </UButton>

            <div class="w-px h-4 bg-slate-700 mx-1 shrink-0" />

            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('bulletList')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                icon="i-lucide-list"
                @click="editor?.chain().focus().toggleBulletList().run()" />
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('orderedList')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                icon="i-lucide-list-ordered"
                @click="editor?.chain().focus().toggleOrderedList().run()" />
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('blockquote')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                icon="i-lucide-quote"
                @click="editor?.chain().focus().toggleBlockquote().run()" />
            <UButton
                variant="ghost"
                size="xs"
                :class="
                    editor?.isActive('codeBlock')
                        ? 'text-accent bg-accent/15'
                        : 'text-slate-400'
                "
                icon="i-lucide-code-2"
                @click="editor?.chain().focus().toggleCodeBlock().run()" />
        </div>

        <!-- Editor content -->
        <div
            class="flex-1 overflow-y-auto min-h-0 px-4 py-3 bg-black/30 backdrop-blur-sm">
            <EditorContent :editor="editor" class="h-full tiptap-editor" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { useEditor, EditorContent } from "@tiptap/vue-3";
import StarterKit from "@tiptap/starter-kit";
import Placeholder from "@tiptap/extension-placeholder";
import Link from "@tiptap/extension-link";
import { Markdown } from "tiptap-markdown";

export interface HeadingItem {
    id: string;
    level: number;
    text: string;
}

interface Props {
    modelValue?: string;
    placeholder?: string;
    label?: string;
}

const props = withDefaults(defineProps<Props>(), {
    modelValue: "",
    placeholder: "開始輸入...",
});

const emit = defineEmits<{
    "update:modelValue": [value: string];
    headings: [headings: HeadingItem[]];
}>();

function slugify(text: string): string {
    return text
        .toLowerCase()
        .replace(/\s+/g, "-")
        .replace(/[^a-z0-9\-\u4e00-\u9fff]/g, "");
}

function extractHeadings(editorInstance: any): HeadingItem[] {
    if (!editorInstance) return [];
    const headings: HeadingItem[] = [];
    const seen = new Map<string, number>();

    editorInstance.state.doc.descendants((node: any) => {
        if (node.type.name === "heading") {
            const text = node.textContent;
            const level = node.attrs.level as number;
            const baseId = slugify(text) || `heading-${headings.length}`;
            const count = seen.get(baseId) ?? 0;
            const id = count === 0 ? baseId : `${baseId}-${count}`;
            seen.set(baseId, count + 1);
            headings.push({ id, level, text });
        }
    });

    return headings;
}

function syncHeadingIds(
    headings: HeadingItem[],
    container: HTMLElement | null,
) {
    if (!container) return;
    const domHeadings = container.querySelectorAll("h1,h2,h3,h4,h5,h6");
    domHeadings.forEach((el, i) => {
        if (headings[i]) el.id = headings[i].id;
    });
}

const editorContainerRef = ref<HTMLElement | null>(null);

const editor = useEditor({
    extensions: [
        StarterKit.configure({
            // StarterKit includes heading, bold, italic, strike, code, etc.
        }),
        Placeholder.configure({
            placeholder: props.placeholder,
        }),
        Link.configure({
            openOnClick: false,
        }),
        Markdown.configure({
            html: false,
            transformPastedText: true,
        }),
    ],
    content: props.modelValue
        ? undefined // set via setContent after mount
        : "",
    editorProps: {
        attributes: {
            class: "outline-none min-h-full",
        },
    },
    onUpdate({ editor: ed }) {
        // Cast storage to any — tiptap-markdown adds .markdown at runtime
        const markdown = (ed.storage as any).markdown.getMarkdown() as string;
        emit("update:modelValue", markdown);

        const headings = extractHeadings(ed as any);
        nextTick(() => {
            const container = editorContainerRef.value?.querySelector(
                ".tiptap",
            ) as HTMLElement | null;
            syncHeadingIds(headings, container);
            emit("headings", headings);
        });
    },
});

// Set initial content once editor is ready
onMounted(() => {
    nextTick(() => {
        if (editor.value && props.modelValue) {
            // tiptap-markdown overrides setContent to parse markdown strings
            editor.value.commands.setContent(props.modelValue, {
                emitUpdate: false,
            } as any);
            // Emit initial headings
            const headings = extractHeadings(editor.value as any);
            nextTick(() => {
                const container = editorContainerRef.value?.querySelector(
                    ".tiptap",
                ) as HTMLElement | null;
                syncHeadingIds(headings, container);
                emit("headings", headings);
            });
        }
    });
});

// Watch for external modelValue changes (e.g. switching notes)
watch(
    () => props.modelValue,
    (newVal) => {
        if (!editor.value) return;
        const current = (
            editor.value.storage as any
        ).markdown.getMarkdown() as string;
        if (current !== (newVal ?? "")) {
            editor.value.commands.setContent(newVal ?? "", {
                emitUpdate: false,
            } as any);
        }
    },
);

onBeforeUnmount(() => {
    editor.value?.destroy();
});
</script>

<style>
/* Tiptap prose styles for dark theme */
.tiptap-editor .tiptap {
    min-height: 100%;
    color: #cbd5e1; /* slate-300 */
    font-size: 0.9375rem;
    line-height: 1.75;
    outline: none;
}

.tiptap-editor .tiptap h1 {
    font-size: 1.5rem;
    font-weight: 700;
    color: #f1f5f9;
    margin-top: 1.5rem;
    margin-bottom: 0.5rem;
    border-bottom: 1px solid rgba(148, 163, 184, 0.15);
    padding-bottom: 0.25rem;
}

.tiptap-editor .tiptap h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #e2e8f0;
    margin-top: 1.25rem;
    margin-bottom: 0.4rem;
}

.tiptap-editor .tiptap h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #e2e8f0;
    margin-top: 1rem;
    margin-bottom: 0.3rem;
}

.tiptap-editor .tiptap h4,
.tiptap-editor .tiptap h5,
.tiptap-editor .tiptap h6 {
    font-weight: 600;
    color: #e2e8f0;
    margin-top: 0.75rem;
    margin-bottom: 0.25rem;
}

.tiptap-editor .tiptap p {
    margin-bottom: 0.75rem;
}

.tiptap-editor .tiptap p:last-child {
    margin-bottom: 0;
}

.tiptap-editor .tiptap strong {
    color: #f1f5f9;
    font-weight: 600;
}

.tiptap-editor .tiptap em {
    color: #94a3b8;
}

.tiptap-editor .tiptap ul,
.tiptap-editor .tiptap ol {
    padding-left: 1.5rem;
    margin-bottom: 0.75rem;
}

.tiptap-editor .tiptap li {
    margin-bottom: 0.25rem;
}

.tiptap-editor .tiptap li > p {
    margin-bottom: 0;
}

.tiptap-editor .tiptap blockquote {
    border-left: 3px solid #475569;
    padding-left: 1rem;
    margin: 0.75rem 0;
    color: #94a3b8;
    font-style: italic;
}

.tiptap-editor .tiptap code {
    background: rgba(148, 163, 184, 0.12);
    border: 1px solid rgba(148, 163, 184, 0.2);
    border-radius: 3px;
    padding: 0.1em 0.35em;
    font-family: ui-monospace, monospace;
    font-size: 0.85em;
    color: #7dd3fc;
}

.tiptap-editor .tiptap pre {
    background: #0f172a;
    border: 1px solid #334155;
    border-radius: 6px;
    padding: 0.875rem 1rem;
    margin: 0.75rem 0;
    overflow-x: auto;
}

.tiptap-editor .tiptap pre code {
    background: none;
    border: none;
    padding: 0;
    color: #e2e8f0;
    font-size: 0.875rem;
}

.tiptap-editor .tiptap a {
    color: #38bdf8;
    text-decoration: underline;
    text-underline-offset: 2px;
}

.tiptap-editor .tiptap a:hover {
    color: #7dd3fc;
}

.tiptap-editor .tiptap hr {
    border: none;
    border-top: 1px solid #334155;
    margin: 1.25rem 0;
}

/* Placeholder */
.tiptap-editor .tiptap p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    float: left;
    color: #475569;
    pointer-events: none;
    height: 0;
}
</style>

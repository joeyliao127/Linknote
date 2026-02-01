<template>
    <div slot="main" class="auth-shell font-jakarta">
        <div class="decor decor-1" aria-hidden="true"></div>
        <div class="decor decor-2" aria-hidden="true"></div>
        <div class="decor decor-3" aria-hidden="true"></div>

        <div
            class="relative mx-auto flex min-h-screen w-full max-w-6xl items-center px-6 py-12 sm:px-10 lg:px-16"
        >
            <div class="grid w-full items-center gap-10 lg:grid-cols-[minmax(0,420px)_1fr]">
                <UCard class="glass-card">
                    <div class="flex items-center gap-3">
                        <div class="brand-mark">LN</div>
                        <div>
                            <p class="text-sm font-semibold text-strong">Linknote</p>
                            <p class="text-xs text-subtle">
                                Knowledge that stays connected.
                            </p>
                        </div>
                        <span class="ml-auto pill">
                            Encrypted
                        </span>
                    </div>

                    <div class="mt-6">
                        <h1 class="text-2xl font-semibold text-strong">
                            Welcome back
                        </h1>
                        <p class="mt-2 text-sm text-muted">
                            Sign in to keep your ideas synced and ready to search.
                        </p>
                    </div>

                    <div class="mt-6">
                        <UTabs :items="tabsForm" color="neutral" class="w-full">
                            <template #signin>
                                <FormSignIn @sign-in="handleSignIn" />
                            </template>
                            <template #signup>
                                <FormSignUp @sign-up="handleSignUp" />
                            </template>
                        </UTabs>
                    </div>

                    <div
                        v-if="authError"
                        class="mt-4 rounded-lg border border-rose-400/30 bg-rose-500/10 px-3 py-2 text-xs text-rose-100"
                    >
                        {{ authError }}
                    </div>

                    <div
                        v-if="isLoading"
                        class="mt-4 flex items-center gap-2 text-xs text-muted"
                    >
                        <span class="h-2 w-2 animate-pulse rounded-full loading-dot"></span>
                        Securing your session...
                    </div>

                    <div class="mt-6 flex items-start gap-2 text-xs text-subtle">
                        <svg
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="1.5"
                            class="mt-0.5 h-4 w-4 icon-tone"
                            aria-hidden="true"
                        >
                            <path
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                d="M16.5 10.5V8.25a4.5 4.5 0 10-9 0V10.5m-1.5 0h12a1.5 1.5 0 011.5 1.5v6a1.5 1.5 0 01-1.5 1.5h-12A1.5 1.5 0 014.5 18v-6a1.5 1.5 0 011.5-1.5z"
                            />
                        </svg>
                        <span>
                            We use encrypted connections and never store plain-text
                            passwords.
                        </span>
                    </div>
                </UCard>

                <div class="space-y-6">
                    <div class="glass-panel">
                        <div class="flex items-center gap-2 text-xs text-subtle">
                            <span class="chip">
                                New
                            </span>
                            <span>AI-assisted capture</span>
                        </div>
                        <h2 class="mt-4 text-3xl font-semibold text-strong">
                            Turn quick thoughts into linked knowledge.
                        </h2>
                        <p class="mt-3 text-sm text-muted">
                            Collect highlights, research, and ideas in one place, then
                            auto-connect them across projects.
                        </p>

                        <div class="mt-6 grid gap-4 sm:grid-cols-2">
                            <div class="stat-card">
                                <p class="text-xs text-subtle">Daily focus</p>
                                <p class="mt-2 text-xl font-semibold text-strong">
                                    3x faster recall
                                </p>
                                <p class="mt-2 text-xs text-subtle">
                                    Smart links surface what you need.
                                </p>
                            </div>
                            <div class="stat-card">
                                <p class="text-xs text-subtle">Team-ready</p>
                                <p class="mt-2 text-xl font-semibold text-strong">
                                    Shared workspaces
                                </p>
                                <p class="mt-2 text-xs text-subtle">
                                    Collaborate without losing context.
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="grid gap-4 sm:grid-cols-2">
                        <div class="feature-card">
                            <div class="flex items-center gap-2">
                                <svg
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    stroke-width="1.5"
                                    class="h-4 w-4 icon-tone"
                                    aria-hidden="true"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        d="M4.5 12l4.5 4.5L19.5 7.5"
                                    />
                                </svg>
                                <p class="text-sm font-semibold text-strong">
                                    Live sync
                                </p>
                            </div>
                            <p class="mt-2 text-xs text-subtle">
                                Notes stay updated across every device.
                            </p>
                        </div>
                        <div class="feature-card">
                            <div class="flex items-center gap-2">
                                <svg
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    stroke-width="1.5"
                                    class="h-4 w-4 icon-tone"
                                    aria-hidden="true"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        d="M12 6v6l4 2"
                                    />
                                </svg>
                                <p class="text-sm font-semibold text-strong">
                                    Instant search
                                </p>
                            </div>
                            <p class="mt-2 text-xs text-subtle">
                                Find answers with semantic recall.
                            </p>
                        </div>
                        <div class="feature-card">
                            <div class="flex items-center gap-2">
                                <svg
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    stroke-width="1.5"
                                    class="h-4 w-4 icon-tone"
                                    aria-hidden="true"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        d="M12 4.5v15m7.5-7.5h-15"
                                    />
                                </svg>
                                <p class="text-sm font-semibold text-strong">
                                    Quick capture
                                </p>
                            </div>
                            <p class="mt-2 text-xs text-subtle">
                                Clip ideas from web, mail, or meeting notes.
                            </p>
                        </div>
                        <div class="feature-card">
                            <div class="flex items-center gap-2">
                                <svg
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    stroke-width="1.5"
                                    class="h-4 w-4 icon-tone"
                                    aria-hidden="true"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        d="M12 3.75l6.75 3.75v7.5L12 18.75 5.25 15v-7.5L12 3.75z"
                                    />
                                </svg>
                                <p class="text-sm font-semibold text-strong">
                                    Secure by design
                                </p>
                            </div>
                            <p class="mt-2 text-xs text-subtle">
                                Granular access controls and audit trails.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { navigateTo, useAuth } from "#imports";
import FormSignIn from "~/components/form/FormSignIn.vue";
import FormSignUp from "~/components/form/FormSignUp.vue";
import type { TabsItem } from "@nuxt/ui";

const { signIn } = useAuth();

const isLoading = ref<boolean>(false);
const authError = ref<string | null>(null);

const tabsForm: TabsItem[] = [
    { label: "Sign In", slot: "signin" as const },
    { label: "Sign Up", slot: "signup" as const },
];

async function handleSignIn(email: string, password: string) {
    isLoading.value = true;
    authError.value = null;

    const result = await signIn(
        {
            email,
            password,
            redirect: false,
        },
        {
            callbackUrl: "/notebooks",
        }
    );

    if (result?.error) {
        authError.value = result.error;
        isLoading.value = false;
        return;
    }

    isLoading.value = false;
    await navigateTo("/notebooks");
}

async function handleSignUp(email: string, username: string, password: string) {
    isLoading.value = true;
    authError.value = null;

    try {
        await $fetch("/api/auth/signUp", {
            method: "POST",
            body: { email, password, username },
        });

        await handleSignIn(email, password);
    } catch (error: any) {
        authError.value =
            error?.data?.message || error?.message || "註冊失敗，請稍後再試";
    } finally {
        isLoading.value = false;
    }
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap");

.font-jakarta {
    font-family: "Plus Jakarta Sans", "Segoe UI", system-ui, sans-serif;
}

.auth-shell {
    position: relative;
    overflow: hidden;
    color-scheme: dark;
    background-color: var(--background);
    background-image:
        radial-gradient(900px circle at 12% 8%, var(--auth-glow-primary), transparent 60%),
        radial-gradient(800px circle at 88% 10%, var(--auth-glow-accent), transparent 55%);
    color: var(--auth-text-strong);
    --auth-text-strong: rgba(248, 250, 252, 0.95);
    --auth-text-muted: rgba(226, 232, 240, 0.72);
    --auth-text-subtle: rgba(226, 232, 240, 0.6);
    --auth-surface: rgba(30, 38, 60, 0.74);
    --auth-surface-strong: rgba(38, 48, 74, 0.86);
    --auth-border: rgba(255, 255, 255, 0.12);
    --auth-glow-primary: rgba(236, 201, 75, 0.28);
    --auth-glow-accent: rgba(251, 146, 60, 0.26);
    --auth-text-strong: color-mix(in oklab, var(--secondary) 92%, white 8%);
    --auth-text-muted: color-mix(in oklab, var(--secondary) 68%, transparent);
    --auth-text-subtle: color-mix(in oklab, var(--secondary) 55%, transparent);
    --auth-surface: color-mix(in oklab, var(--background) 72%, white 28%);
    --auth-surface-strong: color-mix(in oklab, var(--background) 64%, white 36%);
    --auth-border: color-mix(in oklab, var(--secondary) 14%, transparent);
    --auth-glow-primary: color-mix(in oklab, var(--primary) 40%, transparent);
    --auth-glow-accent: color-mix(in oklab, var(--accent) 38%, transparent);
}

.decor {
    position: absolute;
    border-radius: 9999px;
    filter: blur(56px);
    opacity: 0.85;
    pointer-events: none;
}

.decor-1 {
    top: -120px;
    left: -120px;
    width: 280px;
    height: 280px;
    background: var(--auth-glow-primary);
}

.decor-2 {
    bottom: -160px;
    right: -140px;
    width: 320px;
    height: 320px;
    background: var(--auth-glow-accent);
}

.decor-3 {
    top: 40%;
    right: 12%;
    width: 180px;
    height: 180px;
    background: rgba(251, 146, 60, 0.22);
    background: color-mix(in oklab, var(--primary) 35%, var(--accent) 35%);
}

.glass-card,
.glass-panel,
.stat-card,
.feature-card {
    border: 1px solid var(--auth-border);
    background: var(--auth-surface);
    box-shadow:
        0 24px 60px rgba(0, 0, 0, 0.45),
        0 0 0 1px rgba(255, 255, 255, 0.02) inset;
    backdrop-filter: blur(18px);
}

.glass-card {
    padding: 2rem;
    border-radius: 1.5rem;
}

.glass-panel {
    padding: 2rem;
    border-radius: 1.5rem;
}

.stat-card {
    padding: 1.25rem;
    border-radius: 1rem;
}

.feature-card {
    padding: 1.25rem;
    border-radius: 1rem;
}

.stat-card,
.feature-card {
    background: var(--auth-surface-strong);
}

.text-strong {
    color: var(--auth-text-strong);
}

.text-muted {
    color: var(--auth-text-muted);
}

.text-subtle {
    color: var(--auth-text-subtle);
}

.icon-tone {
    color: var(--auth-text-muted);
}

.pill {
    display: inline-flex;
    align-items: center;
    border-radius: 9999px;
    border: 1px solid var(--auth-border);
    background: var(--auth-surface-strong);
    color: var(--auth-text-strong);
    padding: 0.25rem 0.75rem;
    font-size: 11px;
    font-weight: 600;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.35);
}

.chip {
    display: inline-flex;
    align-items: center;
    border-radius: 9999px;
    border: 1px solid var(--auth-border);
    background: rgba(236, 201, 75, 0.16);
    background: color-mix(in oklab, var(--primary) 24%, transparent);
    color: var(--auth-text-strong);
    padding: 0.25rem 0.5rem;
    font-size: 11px;
    font-weight: 600;
}

.loading-dot {
    background: var(--primary);
}

.brand-mark {
    display: inline-flex;
    height: 40px;
    width: 40px;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    background: linear-gradient(135deg, var(--primary), var(--accent));
    color: white;
    font-weight: 600;
    font-size: 0.9rem;
    box-shadow: 0 12px 24px rgba(236, 201, 75, 0.35);
    box-shadow: 0 12px 24px color-mix(in oklab, var(--primary) 40%, transparent);
}

@media (max-width: 1024px) {
    .glass-card,
    .glass-panel {
        padding: 1.5rem;
    }
}

@media (prefers-reduced-motion: reduce) {
    .decor {
        filter: blur(0);
    }
}
</style>

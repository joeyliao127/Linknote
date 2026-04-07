<template>
    <div class="auth-page">
        <!-- Atmospheric background -->
        <div class="auth-bg" aria-hidden="true" />

        <div class="auth-layout">
            <!-- Left: Branding -->
            <div class="auth-left">
                <h1 class="auth-tagline">{{ $t('pages.signIn.tagline') }}</h1>
                <div class="auth-sub">
                    <p class="sub-line sub-left">{{ $t('pages.signIn.buildBrain') }}</p>
                    <p class="sub-line sub-right">
                        {{ $t('pages.signIn.withCode') }}
                        <span class="code-word">CODE</span>
                        {{ $t('pages.signIn.method') }}
                    </p>
                </div>
                <ul class="code-list">
                    <li>
                        <span class="code-letter">C</span>
                        apture
                    </li>
                    <li>
                        <span class="code-letter">O</span>
                        rganization
                    </li>
                    <li>
                        <span class="code-letter">D</span>
                        istill
                    </li>
                    <li>
                        <span class="code-letter">E</span>
                        xpress
                    </li>
                </ul>
            </div>

            <!-- Right: Glass card -->
            <div class="auth-right">
                <div class="glass-card">
                    <!-- Sign In Form -->
                    <template v-if="mode === 'signIn'">
                        <h2 class="form-title">{{ $t('pages.signIn.signInTitle') }}</h2>

                        <UForm
                            :state="signInState"
                            :schema="signInSchema"
                            class="form-body"
                            @submit="onSignIn">
                            <UFormField name="email">
                                <UInput
                                    v-model="signInState.email"
                                    type="email"
                                    :placeholder="$t('pages.signIn.email')"
                                    trailing-icon="i-lucide-mail"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="password">
                                <UInput
                                    v-model="signInState.password"
                                    type="password"
                                    :placeholder="$t('pages.signIn.password')"
                                    trailing-icon="i-lucide-lock"
                                    class="form-input w-full" />
                            </UFormField>

                            <p v-if="authError" class="form-error">
                                {{ authError }}
                            </p>

                            <UButton
                                type="submit"
                                :loading="isLoading"
                                class="form-submit text-sencondary w-full">
                                {{ $t('pages.signIn.signInBtn') }}
                            </UButton>
                        </UForm>

                        <p class="form-switch">
                            {{ $t('pages.signIn.noAccount') }}
                            <button
                                class="switch-link"
                                @click="switchMode('signUp')">
                                {{ $t('pages.signIn.register') }}
                            </button>
                        </p>
                    </template>

                    <!-- Sign Up Form -->
                    <template v-else>
                        <h2 class="form-title">{{ $t('pages.signIn.signUpTitle') }}</h2>

                        <UForm
                            :state="signUpState"
                            :schema="signUpSchema"
                            class="form-body"
                            @submit="onSignUp">
                            <UFormField name="username">
                                <UInput
                                    v-model="signUpState.username"
                                    :placeholder="$t('pages.signIn.username')"
                                    trailing-icon="i-lucide-user"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="email">
                                <UInput
                                    v-model="signUpState.email"
                                    type="email"
                                    :placeholder="$t('pages.signIn.email')"
                                    trailing-icon="i-lucide-mail"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="password">
                                <UInput
                                    v-model="signUpState.password"
                                    type="password"
                                    :placeholder="$t('pages.signIn.password')"
                                    trailing-icon="i-lucide-lock"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="passwordConfirm">
                                <UInput
                                    v-model="signUpState.passwordConfirm"
                                    type="password"
                                    :placeholder="$t('pages.signIn.confirmPassword')"
                                    trailing-icon="i-lucide-lock"
                                    class="form-input w-full" />
                            </UFormField>

                            <p v-if="authError" class="form-error">
                                {{ authError }}
                            </p>

                            <UButton
                                type="submit"
                                :loading="isLoading"
                                class="form-submit text-sencondary w-full">
                                {{ $t('pages.signIn.signUpBtn') }}
                            </UButton>
                        </UForm>

                        <p class="form-switch">
                            {{ $t('pages.signIn.backTo') }}
                            <button
                                class="switch-link"
                                @click="switchMode('signIn')">
                                {{ $t('pages.signIn.signinLink') }}
                            </button>
                        </p>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import * as z from "zod";
import { ref, computed, onBeforeMount } from "vue";
import { navigateTo, useAuth } from "#imports";
import { ValidationMessages } from "#imports";

definePageMeta({ layout: "default", auth: false });

const { t } = useI18n();
const { signIn, status } = useAuth();

const mode = ref<"signIn" | "signUp">("signIn");
const isLoading = ref(false);
const authError = ref<string | null>(null);

// ── Sign In ──────────────────────────────────────────
const signInState = ref({ email: "joey@test.com", password: "test123" });

const signInSchema = z.object({
    email: z.email(ValidationMessages.email.invalid),
    password: z
        .string()
        .nonempty(ValidationMessages.password.required)
        .min(3, ValidationMessages.password.minLength),
});

async function onSignIn() {
    isLoading.value = true;
    authError.value = null;

    const result = await signIn(
        {
            email: signInState.value.email,
            password: signInState.value.password,
            redirect: false,
        },
        { callbackUrl: "/notebooks" },
    );

    if (result?.error) {
        authError.value = result.error;
        isLoading.value = false;
        return;
    }

    isLoading.value = false;
    await navigateTo("/notebooks");
}

// ── Sign Up ──────────────────────────────────────────
const signUpState = ref({
    email: "",
    username: "",
    password: "",
    passwordConfirm: "",
});

const signUpSchema = computed(() =>
    z
        .object({
            email: z.email(ValidationMessages.email.invalid),
            username: z.string().nonempty(ValidationMessages.username.required),
            password: z
                .string()
                .nonempty(ValidationMessages.password.required)
                .min(8, ValidationMessages.password.minLength),
            passwordConfirm: z
                .string()
                .nonempty(ValidationMessages.password.required),
        })
        .refine((d) => d.password === d.passwordConfirm, {
            message: t('pages.signIn.passwordMismatch'),
            path: ["passwordConfirm"],
        }),
);

async function onSignUp() {
    isLoading.value = true;
    authError.value = null;

    try {
        await $fetch("/api/auth/signUp", {
            method: "POST",
            body: {
                email: signUpState.value.email,
                username: signUpState.value.username,
                password: signUpState.value.password,
            },
        });

        signInState.value = {
            email: signUpState.value.email,
            password: signUpState.value.password,
        };
        mode.value = "signIn";
        await onSignIn();
    } catch (error: any) {
        authError.value =
            error?.data?.message || error?.message || t('pages.signIn.signUpFailed');
    } finally {
        isLoading.value = false;
    }
}

// ── Switch ────────────────────────────────────────────
function switchMode(target: "signIn" | "signUp") {
    mode.value = target;
    authError.value = null;
}

// ── Auto-redirect if already logged in ───────────────
onBeforeMount(() => {
    if (status.value === "authenticated") {
        navigateTo("/notebooks");
    }
});
</script>

<style scoped>
@reference "~/assets/css/tailwind.css";

/* ── Page shell ─────────────────────────────────────── */
.auth-page {
    @apply relative min-h-screen w-full overflow-hidden;
    color-scheme: dark;
    color: rgba(248, 250, 252, 0.92);
}

.auth-bg {
    @apply absolute inset-0 z-0 bg-cover bg-center bg-no-repeat;
    background-image: url("/image/fugi.jpeg");
}

/* ── Two-column layout ─────────────────────────────── */
.auth-layout {
    @apply relative z-10 flex items-center justify-center min-h-screen py-12 px-16 mx-auto;
    gap: clamp(14rem, 20vw, 22rem);
    max-width: calc(100vw - 8rem);
}

/* ── Left branding ──────────────────────────────────── */
.auth-left {
    @apply flex-1 max-w-[480px];
}

.auth-tagline {
    @apply text-[3.25rem] font-semibold leading-tight mb-5 whitespace-nowrap;
    color: rgba(248, 250, 252, 0.95);
}

.auth-sub {
    @apply mb-8;
}

.sub-line {
    @apply text-[1.6rem] leading-[1.7] m-0;
    color: rgba(226, 232, 240, 0.8);
}

.sub-right {
    @apply ml-5;
}

.code-word {
    @apply font-bold text-[1.9rem] align-baseline;
    color: var(--primary);
}

.code-list {
    @apply list-none p-0 m-0 flex flex-col gap-2;
}

.code-list li {
    @apply text-[1.3rem] flex items-center gap-[0.1rem];
    color: rgba(226, 232, 240, 0.75);
}

.code-letter {
    @apply font-bold text-[1.35rem] inline-block w-4;
    color: var(--primary);
}

/* ── Right: glass card ─────────────────────────────── */
.auth-right {
    @apply flex items-center justify-center;
}

.glass-card {
    @apply w-[400px] h-[500px] rounded-[20px] bg-transparent border-2 border-white/20 overflow-y-auto flex flex-col;
    padding: 40px 50px 24px 50px; /* asymmetric padding */
    -webkit-backdrop-filter: blur(20px);
    backdrop-filter: blur(20px);
    box-shadow: 0 0 30px rgba(0, 0, 0, 0.2);
}

/* ── Form internals ─────────────────────────────────── */
.form-title {
    @apply text-center text-xl font-semibold mb-6;
    color: rgba(248, 250, 252, 0.95);
}

.form-body {
    @apply flex flex-col gap-4;
}

/* Override Nuxt UI input styles for glass context */
.form-input :deep(input) {
    background: transparent;
    border: none;
    border-bottom: 1.5px solid rgba(255, 255, 255, 0.35);
    border-radius: 0;
    color: rgba(248, 250, 252, 0.9);
    padding-left: 0.5rem;
    transition: border-color 180ms ease;
}

.form-input :deep(input::placeholder) {
    color: rgba(226, 232, 240, 0.5);
}

.form-input :deep(input:focus) {
    outline: none;
    border-bottom-color: rgba(255, 255, 255, 0.7);
    box-shadow: none;
}

.form-input :deep(.i-lucide-mail),
.form-input :deep(.i-lucide-lock),
.form-input :deep(.i-lucide-user) {
    color: rgba(226, 232, 240, 0.6);
}

.form-error {
    @apply text-[0.78rem] -mt-1;
    color: var(--accent);
}

.form-submit {
    @apply mt-0 border-0 rounded font-semibold;
    background: var(--primary);
    transition: background 180ms ease;
}

.form-submit:hover {
    background: var(--primary-dark);
}

.form-switch {
    @apply mt-5 text-center text-[0.82rem];
    color: rgba(226, 232, 240, 0.65);
}

.switch-link {
    @apply font-semibold underline cursor-pointer bg-transparent border-0 p-0;
    color: rgba(248, 250, 252, 0.9);
    font-size: inherit;
    font-family: inherit;
}

.switch-link:hover {
    color: var(--primary);
}

/* ── Responsive ─────────────────────────────────────── */
@media (max-width: 768px) {
    .auth-layout {
        @apply flex-col justify-center items-center py-8 px-6;
        gap: 0;
        max-width: 100vw;
    }

    /* Hide branding on mobile — show form only */
    .auth-left {
        @apply hidden;
    }

    .glass-card {
        @apply w-full max-w-[420px];
        padding: 2rem 2rem 1.5rem;
    }
}
</style>

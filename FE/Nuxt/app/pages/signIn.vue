<template>
    <div class="auth-page">
        <!-- Atmospheric background -->
        <div class="auth-bg" aria-hidden="true" />

        <div class="auth-layout">
            <!-- Left: Branding -->
            <div class="auth-left">
                <h1 class="auth-tagline">Collaborate with partners</h1>
                <div class="auth-sub">
                    <p class="sub-line sub-left">Build your second brain</p>
                    <p class="sub-line sub-right">
                        with
                        <span class="code-word">CODE</span>
                        Method.
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
                        <h2 class="form-title">SignIn</h2>

                        <UForm
                            :state="signInState"
                            :schema="signInSchema"
                            class="form-body"
                            @submit="onSignIn">
                            <UFormField name="email">
                                <UInput
                                    v-model="signInState.email"
                                    type="email"
                                    placeholder="Email"
                                    trailing-icon="i-lucide-mail"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="password">
                                <UInput
                                    v-model="signInState.password"
                                    type="password"
                                    placeholder="Password"
                                    trailing-icon="i-lucide-lock"
                                    class="form-input w-full" />
                            </UFormField>

                            <p v-if="authError" class="form-error">
                                {{ authError }}
                            </p>

                            <UButton
                                type="submit"
                                :loading="isLoading"
                                class="form-submit w-full">
                                SignIn
                            </UButton>
                        </UForm>

                        <p class="form-switch">
                            Don't have an account? Click
                            <button
                                class="switch-link"
                                @click="switchMode('signUp')">
                                Register
                            </button>
                        </p>
                    </template>

                    <!-- Sign Up Form -->
                    <template v-else>
                        <h2 class="form-title">SignUp</h2>

                        <UForm
                            :state="signUpState"
                            :schema="signUpSchema"
                            class="form-body"
                            @submit="onSignUp">
                            <UFormField name="username">
                                <UInput
                                    v-model="signUpState.username"
                                    placeholder="username"
                                    trailing-icon="i-lucide-user"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="email">
                                <UInput
                                    v-model="signUpState.email"
                                    type="email"
                                    placeholder="Email"
                                    trailing-icon="i-lucide-mail"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="password">
                                <UInput
                                    v-model="signUpState.password"
                                    type="password"
                                    placeholder="Password"
                                    trailing-icon="i-lucide-lock"
                                    class="form-input w-full" />
                            </UFormField>

                            <UFormField name="passwordConfirm">
                                <UInput
                                    v-model="signUpState.passwordConfirm"
                                    type="password"
                                    placeholder="Confirm Your Password"
                                    trailing-icon="i-lucide-lock"
                                    class="form-input w-full" />
                            </UFormField>

                            <p v-if="authError" class="form-error">
                                {{ authError }}
                            </p>

                            <UButton
                                type="submit"
                                :loading="isLoading"
                                class="form-submit w-full">
                                SignUp
                            </UButton>
                        </UForm>

                        <p class="form-switch">
                            Back to
                            <button
                                class="switch-link"
                                @click="switchMode('signIn')">
                                Signin
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
import { ref, onBeforeMount } from "vue";
import { navigateTo, useAuth } from "#imports";
import { ValidationMessages } from "#imports";

definePageMeta({ layout: "default", auth: false });

const { signIn, status } = useAuth();

const mode = ref<"signIn" | "signUp">("signIn");
const isLoading = ref(false);
const authError = ref<string | null>(null);

// ── Sign In ──────────────────────────────────────────
const signInState = ref({ email: "", password: "" });

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

const signUpSchema = z
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
        message: "密碼與確認密碼不符",
        path: ["passwordConfirm"],
    });

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
            error?.data?.message || error?.message || "註冊失敗，請稍後再試";
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
/* ── Page shell ─────────────────────────────────────── */
.auth-page {
    position: relative;
    min-height: 100vh;
    width: 100%;
    overflow: hidden;
    color-scheme: dark;
    color: rgba(248, 250, 252, 0.92);
}

.auth-bg {
    position: absolute;
    inset: 0;
    z-index: 0;
    background-image: url("/image/fugi.jpeg");
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
}

/* ── Two-column layout ─────────────────────────────── */
.auth-layout {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 3rem 4rem;
    gap: clamp(14rem, 20vw, 22rem);
    max-width: calc(100vw - 8rem);
    margin: 0 auto;
}

/* ── Left branding ──────────────────────────────────── */
.auth-left {
    flex: 1;
    max-width: 480px;
}

.auth-tagline {
    font-size: 2.75rem;
    font-weight: 600;
    line-height: 1.2;
    color: rgba(248, 250, 252, 0.95);
    margin-bottom: 1.25rem;
    white-space: nowrap;
}

.auth-sub {
    margin-bottom: 2rem;
}

.sub-line {
    font-size: 1.35rem;
    color: rgba(226, 232, 240, 0.8);
    line-height: 1.7;
    margin: 0;
}

.sub-left {
    margin-left: 0px;
}

.sub-right {
    margin-left: 20px;
}

.code-word {
    color: #4ade80;
    font-weight: 700;
    font-size: 1.6rem;
    vertical-align: baseline;
}

.code-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.code-list li {
    font-size: 1.05rem;
    color: rgba(226, 232, 240, 0.75);
    display: flex;
    align-items: center;
    gap: 0.1rem;
}

.code-letter {
    color: #4ade80;
    font-weight: 700;
    font-size: 1.1rem;
    display: inline-block;
    width: 1rem;
}

/* ── Right: glass card ─────────────────────────────── */
.auth-right {
    display: flex;
    align-items: center;
    justify-content: center;
}

.glass-card {
    width: 400px;
    height: 500px;
    border-radius: 20px; /* intentionally not the site's 5px */
    background: transparent;
    border: 2px solid rgba(255, 255, 255, 0.2);
    -webkit-backdrop-filter: blur(20px);
    backdrop-filter: blur(20px);
    box-shadow: 0 0 30px rgba(0, 0, 0, 0.2);
    padding: 40px 50px 24px 50px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
}

/* ── Form internals ─────────────────────────────────── */
.form-title {
    text-align: center;
    font-size: 1.25rem;
    font-weight: 600;
    color: rgba(248, 250, 252, 0.95);
    margin-bottom: 1.5rem;
}

.form-body {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

/* Override Nuxt UI input styles for glass context */
.form-input :deep(input) {
    background: transparent;
    border: none;
    border-bottom: 1.5px solid rgba(255, 255, 255, 0.35);
    border-radius: 0;
    color: rgba(248, 250, 252, 0.9);
    padding-left: 0;
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
    font-size: 0.78rem;
    color: #e30606;
    margin-top: -0.25rem;
}

.form-submit {
    margin-top: 0px;
    background: #22c55e;
    color: #0f1a0f;
    border: none;
    border-radius: 5px;
    font-weight: 600;
    transition: background 180ms ease;
}

.form-submit:hover {
    background: #16a34a;
}

.form-switch {
    margin-top: 1.25rem;
    text-align: center;
    font-size: 0.82rem;
    color: rgba(226, 232, 240, 0.65);
}

.switch-link {
    color: rgba(248, 250, 252, 0.9);
    font-weight: 600;
    text-decoration: underline;
    cursor: pointer;
    background: none;
    border: none;
    padding: 0;
    font-size: inherit;
    font-family: inherit;
}

.switch-link:hover {
    color: #4ade80;
}

/* ── Responsive ─────────────────────────────────────── */
@media (max-width: 768px) {
    .auth-layout {
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 2rem 1.5rem;
        gap: 0;
        max-width: 100vw;
    }

    /* Hide branding on mobile — show form only */
    .auth-left {
        display: none;
    }

    .glass-card {
        width: 100%;
        max-width: 420px;
        padding: 2rem 2rem 1.5rem;
    }
}
</style>

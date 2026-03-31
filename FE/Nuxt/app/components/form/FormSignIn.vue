<template>
    <div class="auth-form">
        <USeparator class="auth-separator">
            <p class="auth-title">Sign In</p>
        </USeparator>

        <div class="flex flex-col gap-6 mb-6">
            <UForm
                :state="state"
                :schema="schema"
                @submit="handleSignIn('email')"
                class="space-y-6 auth-form-body">
                <UFormField label="Email" name="email" :required="true">
                    <UInput
                        v-model="state.email"
                        class="w-full auth-input"
                        type="email"
                        trailing-icon="i-lucide-mail"
                        placeholder="Enter your email" />
                </UFormField>

                <UFormField label="Password" name="password" :required="true">
                    <UInput
                        v-model="state.password"
                        type="password"
                        trailing-icon="i-lucide-lock"
                        placeholder="Enter your password"
                        class="w-full auth-input" />
                </UFormField>

                <UButton
                    variant="solid"
                    label="Continue"
                    type="submit"
                    class="font-semibold w-full auth-primary" />
            </UForm>
        </div>

        <USeparator class="my-4 auth-separator">
            <span class="auth-or">OR</span>
        </USeparator>

        <div class="flex flex-col gap-4">
            <UButton
                icon="uil-google"
                variant="subtle"
                color="neutral"
                class="auth-social"
                @click="handleSignIn('google')">
                Sign in with Google
            </UButton>
            <UButton
                icon="uil-github"
                variant="subtle"
                color="neutral"
                class="auth-social"
                @click="handleSignIn('github')">
                Sign in with GitHub
            </UButton>
        </div>
    </div>
</template>
<script setup lang="ts">
import * as z from "zod";
import { ref } from "vue";
import { ValidationMessages } from "#imports";
import type { Povider } from "~~/types/User";

const emtis = defineEmits(["sign-in"]);

const state = ref({
    email: "joey@test.com",
    password: "test123",
});

const schema = z.object({
    email: z.email(ValidationMessages.email.invalid),
    password: z
        .string()
        .nonempty(ValidationMessages.password.required)
        .min(3, ValidationMessages.password.minLength),
});

function handleSignIn(provider: Povider) {
    emtis("sign-in", state.value.email, state.value.password, provider);
}
</script>

<style scoped>
@reference "~/assets/css/tailwind.css";

.auth-form {
    color: var(--auth-text-strong, var(--secondary, #f8fafc));
}

.auth-title {
    @apply text-center text-xl font-semibold tracking-[0.02em];
    color: var(--auth-text-strong, var(--secondary, #f8fafc));
}

.auth-or {
    @apply text-[0.65rem] tracking-[0.35em];
    color: var(--auth-text-subtle, rgba(226, 232, 240, 0.7));
}

.auth-separator :deep(hr) {
    border-color: var(--auth-border, rgba(255, 255, 255, 0.12));
    opacity: 0.6;
}

.auth-form-body :deep(label) {
    @apply text-[0.7rem] uppercase tracking-[0.12em];
    color: var(--auth-text-subtle, rgba(226, 232, 240, 0.7));
}

.auth-form-body :deep(.auth-input input) {
    background: var(--auth-surface-strong, rgba(30, 38, 60, 0.72));
    border: 1px solid var(--auth-border, rgba(255, 255, 255, 0.12));
    color: var(--auth-text-strong, #f8fafc);
    transition: border-color 180ms ease, box-shadow 180ms ease;
}

.auth-form-body :deep(.auth-input input::placeholder) {
    color: var(--auth-text-subtle, rgba(226, 232, 240, 0.55));
}

.auth-form-body :deep(.auth-input input:focus) {
    border-color: var(--primary, #facc15);
    box-shadow: 0 0 0 2px color-mix(in oklab, var(--primary, #facc15) 30%, transparent);
}

.auth-primary {
    @apply border-0;
    background: linear-gradient(135deg, var(--primary, #facc15), var(--accent, #fb923c));
    color: #16181d;
    box-shadow: 0 14px 30px rgba(15, 23, 42, 0.5);
    transition: transform 180ms ease, box-shadow 180ms ease;
}

.auth-primary:hover {
    transform: translateY(-1px);
    box-shadow: 0 18px 36px rgba(15, 23, 42, 0.6);
}

.auth-social {
    @apply bg-transparent;
    border: 1px solid var(--auth-border, rgba(255, 255, 255, 0.14));
    color: var(--auth-text-strong, #f8fafc);
    transition: background-color 180ms ease, border-color 180ms ease, transform 180ms ease;
}

.auth-social:hover {
    background: var(--auth-surface-strong, rgba(30, 38, 60, 0.72));
    border-color: color-mix(in oklab, var(--primary, #facc15) 40%, transparent);
    transform: translateY(-1px);
}
</style>

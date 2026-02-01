<template>
    <div class="auth-form">
        <UForm :state="state" :schema="schema" class="auth-form-body">
            <USeparator class="auth-separator">
                <p class="auth-title">Sign Up</p>
            </USeparator>

            <div class="flex flex-col gap-6 mb-6">
                <FormInput
                    label="Email"
                    name="email"
                    :required="true"
                    icon="mail"
                    placeholder="Enter your email" />
                <FormInput
                    label="Username"
                    name="username"
                    :required="true"
                    icon="mail"
                    placeholder="Enter your username" />
                <FormInput
                    label="Password"
                    name="password"
                    :required="true"
                    icon="lock"
                    placeholder="Enter your password" />
                <FormInput
                    label="Confirm Password"
                    name="password_confirm"
                    :required="true"
                    icon="lock"
                    placeholder="Enter your password again" />
                <UButton
                    class="w-full justify-center font-semibold auth-primary"
                    @click="emitSignUp">
                    Continue
                </UButton>
            </div>
        </UForm>
    </div>
</template>
<script setup lang="ts">
import * as z from "zod";
import { ref } from "vue";
import { ValidationMessages } from "#imports";

const emits = defineEmits(["sign-up"]);

interface SignUp {
    email: string;
    username: string;
    password: string;
}

const state = ref<SignUp>({
    email: "test1@test.com",
    username: "test1",
    password: "test",
});

const schema = z.object({
    email: z.email(ValidationMessages.email.invalid),
    usernmae: z.string().nonempty(ValidationMessages.username.required),
    password: z
        .string()
        .nonempty(ValidationMessages.password.required)
        .min(8, ValidationMessages.password.minLength),
});

function emitSignUp() {
    emits(
        "sign-up",
        state.value?.email,
        state.value?.username,
        state.value?.password
    );
}
</script>

<style scoped>
.auth-form {
    color: var(--auth-text-strong, var(--secondary, #f8fafc));
}

.auth-title {
    text-align: center;
    font-size: 1.25rem;
    font-weight: 600;
    letter-spacing: 0.02em;
    color: var(--auth-text-strong, var(--secondary, #f8fafc));
}

.auth-separator :deep(hr) {
    border-color: var(--auth-border, rgba(255, 255, 255, 0.12));
    opacity: 0.6;
}

.auth-form-body :deep(label) {
    color: var(--auth-text-subtle, rgba(226, 232, 240, 0.7));
    font-size: 0.7rem;
    text-transform: uppercase;
    letter-spacing: 0.12em;
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
    background: linear-gradient(135deg, var(--primary, #facc15), var(--accent, #fb923c));
    color: #16181d;
    border: none;
    box-shadow: 0 14px 30px rgba(15, 23, 42, 0.5);
    transition: transform 180ms ease, box-shadow 180ms ease;
}

.auth-primary:hover {
    transform: translateY(-1px);
    box-shadow: 0 18px 36px rgba(15, 23, 42, 0.6);
}
</style>

<template>
    <USeparator>
        <p class="text-center text-2xl font-bold">SignIn</p>
    </USeparator>

    <div class="flex flex-col gap-6 mb-6">
        <UForm
            :state="state"
            :schema="schema"
            @submit="handleSignIn('email')"
            class="space-y-6">
            <UFormField label="Email" name="email" :required="true">
                <UInput
                    v-model="state.email"
                    class="w-full"
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
                    class="w-full" />
            </UFormField>

            <UButton
                variant="solid"
                label="Continue"
                type="submit"
                class="font-bold w-full" />
        </UForm>
    </div>

    <USeparator class="my-4"><span>or</span></USeparator>

    <div class="flex flex-col gap-4">
        <UButton
            icon="uil-google"
            variant="subtle"
            color="neutral"
            @click="handleSignIn('google')">
            SignIn with Google
        </UButton>
        <UButton
            icon="uil-github"
            variant="subtle"
            color="neutral"
            @click="handleSignIn('github')">
            SignIn with GitHub
        </UButton>
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

<style></style>

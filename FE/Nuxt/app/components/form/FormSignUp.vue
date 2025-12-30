<template>
    <UForm :state="state" :schema="schema">
        <USeparator>
            <p class="text-center text-2xl font-bold">SignUp</p>
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
                class="justify-center text-white font-bold"
                @click="emitSignUp">
                Continue
            </UButton>
        </div>
    </UForm>
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

<style></style>

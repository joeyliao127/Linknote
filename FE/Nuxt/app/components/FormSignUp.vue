<template>
    <UForm :state="state">
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
            <UButton class="justify-center text-white font-bold">
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

const state = ref<SignUp>();

const schema = z.object({
    email: z.email(ValidationMessages.email.invalid),
    password: z
        .string()
        .nonempty(ValidationMessages.password.required)
        .min(8, ValidationMessages.password.minLength),
});
</script>

<style></style>

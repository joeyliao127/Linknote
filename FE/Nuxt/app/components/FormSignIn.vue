<template>
    <UForm :state="state">
        <USeparator>
            <p class="text-center text-2xl font-bold">SignIn</p>
        </USeparator>

        <div class="flex flex-col gap-6 mb-6">
            <FormInput
                label="Email"
                :required="true"
                icon="mail"
                placeholder="Enter your email" />
            <FormInput
                label="Password"
                :required="true"
                icon="lock"
                placeholder="Enter your password" />
            <UCheckbox label="Remeber Me"></UCheckbox>
            <UButton
                variant="solid"
                label="Continue"
                class="text-white font-bold" />
        </div>
        <USeparator class="my-4"><span>or</span></USeparator>

        <div class="flex flex-col gap-4">
            <UButton
                class="justify-center"
                icon="uil-google"
                variant="subtle"
                color="neutral">
                SignIn with Google
            </UButton>
            <UButton
                class="justify-center"
                icon="uil-github"
                variant="subtle"
                color="neutral">
                SignIn with GitHub
            </UButton>
        </div>
    </UForm>
</template>
<script setup lang="ts">
import { ref } from "vue";

interface SignIn {
    email: String;
    password: String;
}

const state = ref<SignIn>({
    email: "",
    password: "",
});
</script>

<style></style>

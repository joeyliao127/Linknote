<template>
    <div>
        <FormSignTabs />
        <UButton label="登入" @click="handleSignIn" :loading="isLoading" />
        <p v-if="user">{{ user }}</p>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuth } from "#imports";
const { signIn, signUp } = useAuth();

import type { User } from "~/types/User";

const isLoading = ref<boolean>(false);
const user = ref<User>();

async function handleSignIn() {
    isLoading.value = true;
    const resUser = await signIn("joey@gmail.com", "test123");
    user.value = resUser;
    isLoading.value = false;
}
</script>

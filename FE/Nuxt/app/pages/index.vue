<template>
    <div>
        <UCard class="flex gap-8 h-[560px] aspect-[4/5]">
            <div class="w-100">
                <UTabs :items="tabsForm" color="neutral">
                    <template #signin>
                        <FormSignIn @sign-in="handleSignIn" />
                    </template>
                    <template #signup>
                        <FormSignUp @sign-up="handleSignUp" />
                    </template>
                </UTabs>
            </div>
        </UCard>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { navigateTo, useAuth } from "#imports";
import FormSignIn from "~/components/FormSignIn.vue";
import FormSignUp from "~/components/FormSignUp.vue";
import type { TabsItem } from "@nuxt/ui";
import type { Povider } from "~/types/User";
import type { User } from "~/types/User";

const { signIn, signUp } = useAuth();

const isLoading = ref<boolean>(false);
const user = ref<User>();

const tabsForm: TabsItem[] = [
    {
        label: "SignIn",
        slot: "signin" as const,
    },
    {
        label: "SignUp",
        slot: "signup" as const,
    },
];

async function handleSignIn(
    email: string,
    password: string,
    provider: Povider
) {
    isLoading.value = true;
    const resUser = await signIn(email, password);
    user.value = resUser;
    isLoading.value = false;
    console.log(user.value);
    navigateTo("/notebooks");
}

async function handleSignUp(email: string, username: string, password: string) {
    isLoading.value = true;
    const resUser = await signUp(email, username, password);
    user.value = resUser;
    isLoading.value = false;
}
</script>

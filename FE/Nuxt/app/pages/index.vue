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
        <p v-if="authError" class="mt-4 text-red-500 text-sm">
            {{ authError }}
        </p>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { navigateTo, useAuth } from "#imports";
import FormSignIn from "~/components/FormSignIn.vue";
import FormSignUp from "~/components/FormSignUp.vue";
import type { TabsItem } from "@nuxt/ui";

const { signIn } = useAuth();

const isLoading = ref<boolean>(false);
const authError = ref<string | null>(null);

const tabsForm: TabsItem[] = [
    { label: "SignIn", slot: "signin" as const },
    { label: "SignUp", slot: "signup" as const },
];

async function handleSignIn(email: string, password: string) {
    isLoading.value = true;
    authError.value = null;

    const result = await signIn(
        {
            email,
            password,
            redirect: false,
        },
        {
            callbackUrl: "/notebooks",
        }
    );

    if (result?.error) {
        authError.value = result.error;
        isLoading.value = false;
        return;
    }

    // isLoading.value = false;
    // console.log("------");
    // await navigateTo("/notebooks");
}

async function handleSignUp(email: string, username: string, password: string) {
    isLoading.value = true;
    authError.value = null;

    try {
        await $fetch("/api/auth/signUp", {
            method: "POST",
            body: { email, password, username },
        });

        await handleSignIn(email, password);
    } catch (error: any) {
        authError.value =
            error?.data?.message || error?.message || "註冊失敗，請稍後再試";
    } finally {
        isLoading.value = false;
    }
}
</script>

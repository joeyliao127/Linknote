<template>
    <UCard class="flex gap-8 h-[560px] aspect-[4/5]">
        <div class="w-100">
            <UTabs :items="tabsForm" color="neutral">
                <template #signin>
                    <FormSignIn />
                </template>
                <template #signup>
                    <FormSignUp />
                </template>
            </UTabs>
        </div>
    </UCard>
</template>
<script setup lang="ts">
import FormSignIn from "~/components/FormSignIn.vue";
import FormSignUp from "~/components/FormSignUp.vue";
import type { TabsItem } from "@nuxt/ui";
import type { Povider } from "~/types/User";

const emits = defineEmits(["signIn", "signUp"]);

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

function handleSignIn(username: string, password: string, provider: Povider) {
    emits("signIn");
}

function handleSignUp() {
    emits("signUp");
}
</script>

<style></style>

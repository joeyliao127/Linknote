import { ref } from "vue";
import { useAppConfig, useRuntimeConfig } from "#imports";
import type { User } from "@/types/User";

const isValidate = ref(false);
const token = ref<string | null>();

export const useAuth = () => {
    const appConfig = useAppConfig();
    const runtimeConfig = useRuntimeConfig();

    const signIn = async (email: string, password: string) => {
        try {
            const user: User = await $fetch(
                `${runtimeConfig.public.API_URL}/users/signIn`,
                {
                    method: "POST",
                    body: { email, password },
                }
            );

            isValidate.value = true;
            token.value = user.id;
            return { ...user };
        } catch (error) {
            isValidate.value = false;
        }
    };

    const signUp = async (
        email: string,
        password: string,
        username: string
    ) => {
        try {
            const user: User = await $fetch(
                `${runtimeConfig.public.API_URL}/user/signIn`,
                {
                    method: "POST",
                    body: { email, password, username },
                }
            );

            isValidate.value = true;
            token.value = user.id;
            return { ...user };
        } catch (error) {
            isValidate.value = false;
        }
    };

    const signOut = async () => {
        if (!token.value) return true;

        try {
            const response = await $fetch(`/api/user/signOut`, {
                method: "POST",
            });

            isValidate.value = false;
            token.value = null;
        } catch (error) {
            isValidate.value = false;
        }
    };

    return {
        signIn,
        signUp,
        signOut,
    };
};

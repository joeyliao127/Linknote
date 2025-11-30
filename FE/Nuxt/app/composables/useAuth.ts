import { useAppConfig, useRuntimeConfig, useState } from "#imports";
import type { User } from "~~/types/User";

export const useAuth = () => {
    const appConfig = useAppConfig();
    const runtimeConfig = useRuntimeConfig();

    const isValidate = useState<boolean>("isValidate", () => false);
    const token = useState<string | null>("token", () => null);
    const email = useState<string | null>("email", () => null);

    const signIn = async (_email: string, password: string) => {
        try {
            const user: User = await $fetch(
                `${runtimeConfig.public.API_URL}/users/signIn`,
                {
                    method: "POST",
                    body: { email: _email, password },
                }
            );

            isValidate.value = true;
            token.value = user.id;
            email.value = user.email;
            localStorage.setItem("email", user.id);
            localStorage.setItem("userId", user.id);
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
            // const response = await $fetch(`/api/user/signOut`, {
            //     method: "POST",
            // });

            isValidate.value = false;
            token.value = null;
            email.value = null;
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

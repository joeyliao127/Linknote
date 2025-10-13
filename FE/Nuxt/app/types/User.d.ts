export interface User {
    id: string;
    username: string;
    email: string;
    enabled: boolean;
    createdAt: string;
    updatedAt: string;
}

export type Povider = "email" | "google" | "github";

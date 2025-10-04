import type { ZodEmail } from "zod";

export interface User {
    id: string;
    username: string;
    email: ZodEmail;
    enabled: boolean;
    createdAt: string;
    updatedAt: string;
}

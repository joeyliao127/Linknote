import type { ErrorCode } from "~~/shared/error/ErrorCode";
import type { ErrorBody } from "./ServerErrorBody";
// shared/errors/error-codes.ts
export const AuthErrorCodes: Record<string, ErrorBody> = {
    UNAUTHORIZED: {
        errorCode: "UNAUTHORIZED",
        statusCode: 401,
        message: "Authentication required",
    },
};

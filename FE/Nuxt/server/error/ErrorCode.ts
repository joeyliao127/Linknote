// shared/error/ErrorCode.ts
export const ErrorCodes = {
    // Authentication Errors
    UNAUTHORIZED: "UNAUTHORIZED",

    // General Errors
    INTERNAL_SERVER_ERROR: "INTERNAL_SERVER_ERROR",
    SERVICE_UNAVAILABLE: "SERVICE_UNAVAILABLE",
    FORBIDDEN: "FORBIDDEN",

    // Resource Errors
    NOTEBOOK_NOT_FOUND: "NOTEBOOK_NOT_FOUND",
    MAX_NOTEBOOKS_REACHED: "MAX_NOTEBOOKS_REACHED",
    NOTE_NOT_FOUND: "NOTE_NOT_FOUND",
    TAG_NOT_FOUND: "TAG_NOT_FOUND",
} as const;

export type ErrorCode = (typeof ErrorCodes)[keyof typeof ErrorCodes];

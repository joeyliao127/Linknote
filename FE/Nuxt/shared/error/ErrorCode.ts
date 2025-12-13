/**
 * TODO:
 * 1. 統一 error code 定義位置，但目前BFF import 會有 path 問題
 * 2. 前端和後端個維護一份 error code 定義，必須要同步內容
 *
 */
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

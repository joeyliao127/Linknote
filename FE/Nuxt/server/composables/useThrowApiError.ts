import { ErrorCode } from "~~/shared/error/ErrorCode";
import { ErrorMap } from "~~/server/error/ServerErrorMap";

export function useThrowApiError(errorCocde: ErrorCode) {
    createError({
        statusCode: ErrorMap[errorCocde]?.statusCode || 500,

        // 回傳 response body 給前端
        statusMessage: ErrorMap[errorCocde]?.message || "Internal Server Error",
    });
}

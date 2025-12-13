import type { ErrorCode } from "~~/shared/error/ErrorCode";
import type { ErrorBody } from "./ServerErrorBody";

export const GeneralErrorCodes: Record<string, ErrorBody> = {
    INTERNAL_SERVER_ERROR: {
        errorCode: "INTERNAL_SERVER_ERROR",
        statusCode: 500,
        message: "Internal Server Error",
    },
    SERVICE_UNAVAILABLE: {
        errorCode: "SERVICE_UNAVAILABLE",
        statusCode: 500,
        message: "Service Unavailable",
    },
    FORBIDDEN: {
        errorCode: "FORBIDDEN",
        statusCode: 403,
        message: "FORBIDDEN",
    },
};

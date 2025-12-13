import type { ErrorCode } from "~~/shared/error/ErrorCode";

export type ErrorBody = {
    errorCode: ErrorCode;
    statusCode: number;
    message: string;
};

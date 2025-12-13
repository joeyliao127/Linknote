import type { ErrorBody } from "./ServerErrorBody";
import { AuthErrorCodes } from "./AuthErrorCode";
import { ResourceErrorCodes } from "./ResourceErrorCode";
import { GeneralErrorCodes } from "./GernelErrorCode";
import { ErrorCode } from "~~/shared/error/ErrorCode";

export const ErrorMap: Record<string, ErrorBody> = {
    ...AuthErrorCodes,
    ...ResourceErrorCodes,
    ...GeneralErrorCodes,
};

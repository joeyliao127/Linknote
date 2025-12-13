import type { ErrorCode } from "#shared/error/ErrorCode";
import { ErrorCodes } from "#shared/error/ErrorCode";
import type { ErrorBody } from "./ServerErrorBody";

// 不寫多層 object，是為了簡化 ts type 定義，因此用 comment 對 resource 做分類
export const ResourceErrorCodes: Record<string, ErrorBody> = {
    // notebooks
    NOTEBOOK_NOT_FOUND: {
        errorCode: ErrorCodes.NOTEBOOK_NOT_FOUND,
        statusCode: 401,
        message: "Authentication required",
    },
    MAX_NOTEBOOKS_REACHED: {
        errorCode: ErrorCodes.MAX_NOTEBOOKS_REACHED,
        statusCode: 400,
        message: "Maximum number of notebooks reached",
    },

    // notes
    NOTE_NOT_FOUND: {
        errorCode: ErrorCodes.NOTE_NOT_FOUND,
        statusCode: 400,
        message: "Note not found",
    },

    // tags
    TAG_NOT_FOUND: {
        errorCode: ErrorCodes.TAG_NOT_FOUND,
        statusCode: 400,
        message: "Tag not found",
    },
};

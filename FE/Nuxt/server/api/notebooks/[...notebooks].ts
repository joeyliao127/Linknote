import { useThrowApiError } from "~~/server/composables/useThrowApiError";
import { ErrorCodes } from "~~/server/error/ErrorCode";

export default defineEventHandler(() => {
    useThrowApiError(ErrorCodes.FORBIDDEN);
});

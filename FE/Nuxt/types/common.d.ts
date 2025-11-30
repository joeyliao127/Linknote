export type ApiResult<T> =
    | { ok: true; data: T }
    | { ok: false; error: ApiError };

export interface ApiError {
    message: string;
    statusCode?: number;
    details?: Record<string, any>;
}

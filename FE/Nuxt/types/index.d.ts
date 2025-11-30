export interface Pagination<T> {
    count: number;
    currentPage: number;
    pageSize: number;
    totalPage: number;
    items: T[];
}

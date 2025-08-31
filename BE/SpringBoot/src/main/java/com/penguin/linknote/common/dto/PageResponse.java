package com.penguin.linknote.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private Integer count;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPage;
    private List<T> items;

    public PageResponse() {}
}

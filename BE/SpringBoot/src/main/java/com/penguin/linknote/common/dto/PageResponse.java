package com.penguin.linknote.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
public class PageResponse<R> {
    private Integer count;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPage;
    private List<R> items;

    public static  <R, T> PageResponse<R> fromPage(Page<T> page, Function<T, R> mapper) {
        PageResponse<R> pageResponse = new PageResponse<R>();

        pageResponse.setCount(page.getNumberOfElements());
        pageResponse.setCurrentPage(page.getNumber());
        pageResponse.setPageSize(page.getSize());
        pageResponse.setTotalPage(page.getTotalPages());
        pageResponse.setItems(page.getContent().stream().map(mapper).toList());
        return pageResponse;
    }
}

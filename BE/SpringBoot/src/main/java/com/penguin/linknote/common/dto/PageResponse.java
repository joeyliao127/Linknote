package com.penguin.linknote.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResponse<R> {
    private Integer count;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPage;
    private List<R> items;

}

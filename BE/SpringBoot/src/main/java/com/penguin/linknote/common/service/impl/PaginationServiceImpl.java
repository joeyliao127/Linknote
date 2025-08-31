package com.penguin.linknote.common.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;


@Service
public class PaginationServiceImpl implements PaginationService {

    @Value("${maxPageSize}")
    private Integer maxPageSize;

    @Value("${defaultPageSize}")
    private Integer defaultPageSize;

    @Override
    public <T, R> PageResponse<R> applyPagination(JPAQuery<T> jpaQuery, PageCommand pageCommand, Function<T, R> mapper) {
        PageCommand normalizeCommand = normalizePageCommand(pageCommand);

        // clone totalQuery 是為了避免執行 fetchCount() 時，修改了原本 jpaQuery 內部 sql
        // 若執行 fetchCount，sql 會移除 limit, offset, orderBy，並且修改 select -> select(*)
        int total = (int) jpaQuery.clone().fetchCount();

        Integer totalPage = calculateTotalPage(total, normalizeCommand.getPageSize());

        List<T> resultList = jpaQuery.limit(normalizeCommand.getPageSize())
                .offset((long) (normalizeCommand.getPage() - 1) * normalizeCommand.getPageSize())
                .fetch();

        PageResponse<R> pageResponse = new PageResponse<R>();
        pageResponse.setItems(resultList.stream().map(mapper).toList());
        pageResponse.setCount(resultList.size());
        pageResponse.setCurrentPage(normalizeCommand.getPage());
        pageResponse.setPageSize(normalizeCommand.getPageSize());
        pageResponse.setTotalPage(totalPage);
        return pageResponse;
    }

    private Integer calculateTotalPage(int total, int pageSize) {
        return (int) Math.ceil((double) total / pageSize);
    }

    private PageCommand normalizePageCommand(PageCommand pageCommand) {
        if (pageCommand == null) pageCommand = new PageCommand();

        if (pageCommand.getPage() == null) pageCommand.setPage(1);

        if (pageCommand.getPageSize() == null) pageCommand.setPageSize(defaultPageSize);
        else if (pageCommand.getPageSize() <= 0) pageCommand.setPageSize(defaultPageSize);
        else if (pageCommand.getPageSize() > 100) pageCommand.setPageSize(maxPageSize);
        return pageCommand;
    }
}

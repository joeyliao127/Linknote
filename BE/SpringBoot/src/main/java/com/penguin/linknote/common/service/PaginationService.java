package com.penguin.linknote.common.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.List;
import java.util.function.Function;

public interface PaginationService {
     <T, R> PageResponse <R> applyPagination(JPAQuery<T> jpaQuery, PageCommand pageCommand, Function<T, R> mapper);
}

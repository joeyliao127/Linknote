package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.notebook.NotebookCondition;
import com.penguin.linknote.entity.Notebook;

public interface NotebookRepository {
    List<Notebook> index(UUID userId, Integer limit);

    PageResponse<Notebook> paginate(int page, int limit, NotebookCondition condition);

    Optional<Notebook> get(UUID id);

    Notebook create(Notebook notebook);

    Notebook update(Notebook notebook);

    void delete(UUID id);
}

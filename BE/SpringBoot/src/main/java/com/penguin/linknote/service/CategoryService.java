package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.category.CategoryCreateCommand;
import com.penguin.linknote.domain.category.CategoryDTO;
import com.penguin.linknote.domain.category.CategoryFilter;
import com.penguin.linknote.domain.category.CategoryUpdateCommand;
import java.util.UUID;

public interface CategoryService {
    // TODO: 支援 star, tag, order by desc 等 filters
    PageResponse<CategoryDTO> index(CategoryFilter filter, PageCommand pageCommand);

    CategoryDTO create(CategoryCreateCommand categoryCreateCommand);

    CategoryDTO update(UUID categoryId, CategoryUpdateCommand categoryUpdateCommand);

    void delete(UUID categoryId);
}

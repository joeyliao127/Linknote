package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.category.CategoryCreateCommand;
import com.penguin.linknote.domain.category.CategoryDTO;
import com.penguin.linknote.domain.category.CategoryFilter;
import com.penguin.linknote.domain.category.CategoryUpdateCommand;
import com.penguin.linknote.entity.Category;
import com.penguin.linknote.entity.QCategory;
import com.penguin.linknote.repository.CategoryRepository;
import com.penguin.linknote.service.CategoryService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final PaginationService paginationService;

    public CategoryServiceImpl(
        CategoryRepository categoryRepository,
        JPAQueryFactory jpaQueryFactory,
        PaginationService paginationService
    ) {
        this.categoryRepository = categoryRepository;
        this.jpaQueryFactory = jpaQueryFactory;
        this.paginationService = paginationService;
    }

    @Override
    public PageResponse<CategoryDTO> index(CategoryFilter filter, PageCommand pageCommand) {
        QCategory qCategory = QCategory.category;

        // TODO: 完成 q 物件查詢
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // if(title != null) booleanBuilder.and(qCategory.title.eq(title));
        // if(active != null) booleanBuilder.and(qCategory.isActive.eq(active));

        JPAQuery<Category> categoryJPAQuery = jpaQueryFactory
            .selectFrom(qCategory)
            .where(booleanBuilder)
            .orderBy(qCategory.updatedAt.asc());

        return paginationService.applyPagination(categoryJPAQuery, pageCommand, CategoryDTO::fromEntity);
    }

    @Override
    public CategoryDTO create(CategoryCreateCommand categoryCommand) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        return CategoryDTO.fromEntity(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO update(UUID categoryId, CategoryUpdateCommand categoryCommand) {
        Category existingCategory = categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existingCategory.setId(categoryId);
        existingCategory.setUpdatedAt(Instant.now());

        return CategoryDTO.fromEntity(categoryRepository.save(existingCategory));
    }

    @Override
    public void delete(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}

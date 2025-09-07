# Architecture & Convention 
採用 MVC 架構，Model 使用 JPA + DSL 進行查詢。

---

## Controller 
- 驗證前端參數
- GET Request 若帶有三個以上的 Query String，使用 @ModelAttribute 接收 Query String
- GET Request Pagination 使用 `PageCommand` 接收 Query String

### API Response
- Error 統一交由 Exception Handler 處理，並統一回覆 `ApiResponse`。

---

## Service
- Domain 相關 service，允許 DI 表中 FK 參考的 Repository，若沒有直接關係又得使用，DI 相關的 Service 在進行調用。

example
- 由於 notebooks table 有 userId 作為 FK，因此 NotebookService 允許 DB UserRepository

---

## Model
查詢方式選擇
- 簡單的查詢或單表 JOIN 查詢使用 JPA
- 多表查詢、多條件查詢、GROUP BY 等，使用 DSL
- Window Function, CTE 使用 @NativeQuery

### Pagination
- 提供 `PaginationService` Bean 輕鬆處理 DSL 的分頁，`applyPagination` 只需要傳入 `JpaQuery` + `PageCommand` + `Mapper`，Service 將會自動 normalize PageCommand 並處理分頁邏輯，即便傳入空的 `PageCommand` 也可以。
- `Mapper` 採用 Strategy Pattern，從外部傳入轉換為 DTO 的 callback 到 `applyPagination` 處理轉換邏輯。
- `applyPageination` return `PageResponse<T>`

example
```java
@Override
    public PageResponse<NotebookDTO> indexNotebooks(UUID userId, String title, Boolean active, PageCommand pageCommand) {
        QNotebook qNotebook = QNotebook.notebook;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(userId != null) booleanBuilder.and(qNotebook.user.id.eq(userId));
        if(title != null) booleanBuilder.and(qNotebook.title.eq(title));
        if(active != null) booleanBuilder.and(qNotebook.isActive.eq(active));

        JPAQuery<Notebook> notebookJPAQuery = jpaQueryFactory
                .selectFrom(qNotebook)
                .where(booleanBuilder);

        return paginationService.applyPagination(notebookJPAQuery, pageCommand, NotebookDTO::fromEntity);
    }
```

JPA 的分頁使用原生的 Pageable，但是前端依然使用 `PageCommand` 接收 Query String， 並使用 `PaginationService` 將 command normalize 後，使用 JPA 的 `PageRequest.of` 建立 `Pageable` 再傳入 JPA。

最後使用 `PageResponse.fromPage()` 轉換 JPA `Page<T>` 為 `PageReponse<T>`。

example
```java
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.penguin.linknote.common.command.PageCommand;

public PageResponse<TagDTO> indexTags(UUID userId, UUID noteId, PageCommand pageCommand) {
    PageCommand normalPageCommand = paginationService.normalizePageCommand(pageCommand);
    Pageable pageable = PageRequest.of(normalPageCommand.getPage(), normalPageCommand.getPageSize());
    Page<Tag> tagList = tagRepository.findByUserId(noteId, pageable);

    return  PageResponse.fromPage(tagList, TagDTO::fromEntity);
}
```
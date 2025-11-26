# 驗證/授權

使用 Spring Seucrity 進行驗證與授權，以下是架構說明

-   使用 JWT Token
-   使用 RBAC Model
    -   Resource: type 定義在 resources table，代表資源種類，如 notebook, note
        -   目前的 resources: notebook, note
    -   Operation: type 定義在 operations table，代表操作行為，如修改 note, 刪除 note
        -   目前的 operations: READ, CREATE, UPDATE, DELETE
    -   Role:
        -   目前的 Role: ROLE_OWNER, ROLE_COLLABORATOR, ROLE_MEMBER, ROLE_GUEST

## Spring Security 設定

-   JWT Hash 用的 key 從 JWTConfig 注入
-   Authentication

## request 生命週期

user 發送 request 後，經歷 spring security chain 直到 controller 的說明

### Authentication

step1：驗證 token 合法性與取得 user 資訊

request 會先經過 authention，透過自訂的 JWTAuthenticationFilter 驗證 token

    - 此階段會 parse token 中的 user，並傳入 SecurityContextHolder，讓後續的授權、Controller 可
    以調用到 token 中帶上的資訊，或者其他 user 資訊 (因為驗證會查 DB，可以順便寫入需要的資訊)。

    - 傳遞到下一個 filter

### Authorization

step2：驗證使用者是否有權限訪問資源

WebSecurity 會先放行白名單 APIs，接著進行有登記需要驗證授權的 api，並且交給 `RBACAuthorizationManager` 進行路徑分析與授權驗證。

`RBACAuthorizationManager` 會根據 request path 解析出 resource 與 operation，接著查詢 resource_acl 表確認 userId 是否有權限，最終 return boolean。

### Controller

step3：通過驗證與授權的 request，最終會進入 controller 進行操作。

在 controller 可以在 controller method params 中，加上 `Authentication` 參數讓 Spring 自動加入驗證資訊，取得在驗證階段傳入的 user 資訊，執行後需操作。

Example

```java
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/notebooks")
@Validated
public class NotebookController {
@GetMapping
    public ResponseEntity<PageResponse<NotebookDTO>> index(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean active,
            Authentication authentication,
            PageCommand pageCommand)
    {
        // @ModelAttribute(忽略沒寫，用於pageCommand) 用於 Get Method，可以自動接收 Query String 到 Object 中，但 Object 必須有 no arg constructor
        UUID userId = (UUID) authentication.getPrincipal();
        PageResponse<NotebookDTO> notebookDTOList = notebookService.index(userId, title, active, pageCommand);
        return ResponseEntity.ok(notebookDTOList);
    }
}
```

## 新增 Resource 授權

1. 在 `WebSecurity` 中加上 `requestMatchers(${api_path}).access(rbacManager)`
2. DB 要 INSERT Role 對於 resource 可以進行什麼 operation
    - insert resource_acl
    - 定義新的 resource (optional)
    - 定義新的 operation (optional)
    - 定義新的 role (optional)

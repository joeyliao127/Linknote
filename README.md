# LinkNote

這是一款線上筆記軟體，核心特色是以 RBAC 分享筆記本並支援 Markdown 筆記編輯與預覽。

-   RBAC 角色（Owner/Collaborator/Member/Guest）分享筆記本與筆記
-   Markdown 編輯與預覽，適合技術筆記與文件整理
-   筆記與標籤多條件篩選

Website URL: https://aquaboxs.com

可用測試帳號登入：

帳號: test@test.com 密碼: abc123

## 技術總覽

-   前端：Nuxt 4（SSR + Server Routes BFF）、Composition API
-   後端：Spring Boot、DDD-lite 分層結構
-   認證：JWT + Spring Security Filter Chain
-   授權：RBAC + Resource-level ACL（每個資源皆可細粒度控管）
-   資料庫：PostgreSQL
-   查詢：JDBC（NamedParameterJdbcTemplate）
-   開發環境：Docker Compose

## 架構概覽

-   瀏覽器 -> Nuxt BFF（server/api）-> Spring Boot -> PostgreSQL
-   JWT 由 BFF 安全保存，瀏覽器僅保存 session id，降低 XSS 風險
-   BFF 可做資料組裝與快取，減少前端直接接觸敏感資訊
-   授權以 Resource-based ACL 為核心，Notebook/Note/Tag 都可獨立授權

## RBAC 分享筆記本

LinkNote 以「角色 + 資源」的 RBAC 設計分享筆記本，並用 ACL 做到每個資源的細粒度控管。

-   角色定義：Owner/Collaborator/Member/Guest
-   每個資源都會有 ACL 紀錄，例如：

```text
user_id | role_id | resource_id | target_id
```

當使用者被加入筆記本後，會依角色決定能讀取、編輯或管理該筆記本底下的筆記與標籤。

## Markdown

所有筆記以 Markdown 為主要內容格式，支援常見語法（標題、清單、程式碼區塊等），適合整理技術筆記與文件。

## 架構決策紀錄

-   `docs/architecture/ArchitectureDecisionRecord.md`

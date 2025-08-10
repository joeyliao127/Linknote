# 介紹

快速建立前後端分離專案模板。

## Convention

-   Conding
-   File Name
-   Schema
-   Git

### Schmea

根據 SQL 表準命名

-   Table 命名使用 snake_case，並使用複數形式。 e.g. note_tags
-   Column 命名使用 snake_case，並使用複數形式。 e.g. user_id
-   Reference 必須使用 Foreign Key
-   自動遞增的 PK，請使用 `GENERATED ALWAYS / DEFAULT AS IDENTITY`，不使用 `SERIAL` 或 `AUTO_INCREMENT`
    -   用意：
        -   SQL 標準語法
        -   明確定義 id 的新增可否由外部新增，或只限定 DB 自動產生
-   欄位擺放順序
    -   id(PK) -> 資料 -> created_at, updated_at -> FK -> FK constraint -> unique -> PK 設定

### Git

統一 git commit 訊息，設定 commit log template

```zsh
git config commit.template .gitmessage.txt
```

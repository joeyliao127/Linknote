# SQL 查詢方式

-   JPA
-   QDSL
-   Native SQL

我現在要先定義好專案對於 sql 的查詢規則，目前專案有使用 JPA, QDSL, NativeSQL，我想整理這三種方式使用的場景，用於未來 AI 協作的規範文件。

關於 connection，只透過 JPA 當作唯一的連線方式，不另外使用 JDBC 建立連線，不維護兩套 DB 連線方式。

## JPA

使用 JPA 進行簡單查詢、INSERT、UPDATE、DELETE 等操作，不透過 JPA 維護 table relation，table relation 還是要看 ERD 為主

目的：快速執行簡單的 sql 查詢，並且簡化 create, update, delete 等 function

限制：JOIN 操作一率交給 NativeSQL，JPA 不維護 relation

## QDSL

當 Query 需要組合 Condition 時，改用 QDSL 查詢目的：優雅的 append condition 並組合 SQL，並支援 paginationService.applayPaginate() 分頁格式。

目的：省去手動組合 condition 字串出錯的風險與時間

## Native SQL

當需要 JOIN table 時，直接使用原生查詢

實作方式：

-   建立 ${name}QueryRepository, ${name}QueryRepostioryImpl
-   使用 DI 的方式取得 JPA 建立的 connection

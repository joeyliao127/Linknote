📘 Linknote - Architecture Decision Records (ADR Index)

本文件記錄 Linknote 專案所有的重要架構決策與理由。所有 ADR 皆採用編號（ADR-001 ~），並遵循「決策不可刪除，只能廢止（Superseded）」原則。

⸻

ADR-001 — Adopt Spring Boot as Backend Framework

Context

Linknote 需要一個可擴展、可測試、可模組化的後端架構，並整合 RBAC、JWT、資料庫、多層架構與 DDD。

Decision

選擇 Spring Boot 作為後端框架。

Rationale • 生態強大（Security, JPA, Redis…） • 統一的 DI/IoC 管理 • 適合中大型系統的架構清晰度 • 測試、擴展性、觀察性都更成熟

Consequences • 初期學習曲線較高 • 系統具有更高彈性，適合未來擴展為多服務架構

⸻

ADR-002 — Adopt Nuxt 3 as Frontend Framework

Context

前端需要 SSR、SEO、良好 DX、可組件化 UI 開發。

Decision

選擇 Nuxt 3 作為前端框架。

Rationale • Server routes (BFF) 天然支援 • 組件化 + Composition API 適合 MVVM 架構 • 支援 API proxy、runtime config、middleware

Consequences • build 成本比 SPA 高 • 需理解 Nitro/Nuxt Server 運作模式

⸻

ADR-003 — Choose PostgreSQL as Primary Database

Context

資料量龐大，需支援 RBAC、複雜查詢、JOIN、多中介表、pagination、並計畫日後做推薦系統。

Decision

採用 PostgreSQL。

Rationale • 原生 UUID 支援 • JSONB、CTE、Window Functions 國內外大服務都採 • 無鎖 alter table、穩定度比 MySQL 更強

Consequences • 需學習些許 PostgreSQL 特有語法 • DB 調校方式與 MySQL 不完全相同

⸻

ADR-004 — Adopt Docker Compose for Dev Environment

Context

團隊希望「拉下專案 → Docker Compose up → 開發」避免本機安裝任何 DB 或後端。

Decision

使用 Docker Compose 管理 dev 全部服務。

Rationale • FE/BE/DB/Init 容器化，干淨一致 • 不依賴開發機環境 • 更容易做 CI/CD

Consequences • 本機跑 Docker 的 RAM/CPU 壓力較大

⸻

ADR-005 — Adopt BFF (Backend For Frontend) via Nuxt Server

Context

需要安全儲存 JWT、避免將敏感資訊曝光前端。

Decision

所有瀏覽器 → API 請求都走 Nuxt BFF（server/api/）\*\* 由 BFF 存放 JWT，瀏覽器僅保存 session id。

Rationale • 瀏覽器不接觸 JWT → 避免 XSS 竊取 • BFF 可做 caching、資料組裝、Rate Limit • 更符合大型服務前端 gateway 實務

Consequences • 多一層 API controller（可用腳本產生避免麻煩） • BFF 成為額外維護點（但安全值 + 可控性提高）

⸻

ADR-006 — Use JWT + Spring Security for Authentication

Context

需要無狀態身份驗證、RBAC 授權需要 userId。

Decision

使用 JWT + Spring Security Filter Chain 自訂： • JWTAuthenticationFilter • AuthFacade • RBACAuthorizationManager

Rationale • 與 REST API 相容性最高 • 無需 session store，擴展性好 • Security 支援 Filter → Authentication → Authorization 完整鏈路

Consequences • 必須清楚掌握 Spring Security lifecycle（filter ordering） • Debug 難度比一般 Express/Nuxt 略高

⸻

ADR-007 — Adopt Resource-Level ACL for Authorization

Context

Linknote 需要 Notebook → Notes → Tag 的階層式授權控制。

Decision

採用 Resource-based ACL（每個 target_id 都有一筆權限）

schema：

| user_id | role_id | resource_id | target_id |

Rationale • 每筆筆記 / 每本筆記本 都能細粒度授權 • 支援 owner, collaborator, member 角色 • 未來新增 Folder 或其他資源也不會破壞架構

Consequences • ACL 會大量增加資料（但可以 index 優化） • PermissionService query 稍複雜

⸻

ADR-008 — Adopt DDD-Lite Folder Structure

Context

需要將 domain 邏輯、controller、entity、service 清楚分類。

Decision

採用 DDD-lite 階層：

/domain /user /note /notebook /rbac /controller /service /entity /repository

Rationale • domain 清楚 → 開發心智模型降低 • 避免 Fat Service、Fat Controller • 設計可自然成長為 microservice 結構

Consequences • 初期檔案多但可維護性極高

⸻

ADR-009 — Use QueryDSL for Dynamic Querying

Context

Linknote 有大量複雜查詢，例如： • notebook + notes + tags • owner/filter/pagination • RBAC multi-join

Decision

使用 QueryDSL 製作動態查詢。

Rationale • type-safe • 不需要手寫字串 SQL • BooleanBuilder 適合 Filter-Based Query

Consequences • 需額外學習 QueryDSL 語法 • 架 CI 時需加入 Q-class 產生

⸻

ADR-010 — Adopt Clean Exception Handling with Domain-Specific Handlers

Context

需要清楚分離各 domain 的錯誤處理，如 user, note, invitation。

Decision

每個 domain 有自己的 ExceptionHandler：

@RestControllerAdvice(basePackages = "com.penguin.linknote.controller.user")

Rationale • 分層乾淨，錯誤原因易追蹤 • 只處理各自 controller 拋出的錯誤 • 全域仍保留 Global Handler 最終兜底

Consequences • 需維護多個 Handler（但結構更清晰）

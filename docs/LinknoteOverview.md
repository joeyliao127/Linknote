Linknote Overview

Linknote 是一款專注於「知識管理」「筆記版本演化」「協作」的線上筆記系統。本文件描述系統現況、現有功能、技術選型、架構、資料模型，以及未來的強化方向（如 RAG、共編、MCP）。

⸻

🧩 核心價值 1. 讓使用者能用最自然的方式紀錄知識（文章、Highlight、Tagging） 2. 讓筆記之間形成「網絡」而非階層（連結、標記、群組） 3. 提供高效的查詢與推薦機制（向量資料庫、RAG） 4. 支援多人協作與權限控管（RBAC、分享、協作） 5. 朝向 AI Agent 與 MCP 不斷擴充的知識平台

⸻

📌 現有功能（2025 年現況）

### 1. User & Auth

    •	JWT 驗證（Spring Security FilterChain）
    •	RBAC 權限管理（角色 + 資源 + 操作 + ACL）
    •	Side-based Auth（BFF 型 Session 管理）

2. Notebook 筆記本 • CRUD • 透過 ACL 控管 owner/collaborator 的權限 • 自動建立 owner 角色綁定

3. Note 筆記 • CRUD • 多對多 Tags • 高亮文字（Highlight） • 自動更新 updatedAt • ACL 維護與筆記可見性檢查

4. Tag 系統 • 多筆記共享 Tag • Note <-> Tag 維護複合 PK（NoteTagId） • Tags 查詢與搜索

5. Invitation 共享機制 • 使用者之間可以邀請合作編輯 Notebook • State Machine（Pending / Accepted / Rejected） • InvitationService + 狀態轉換物件封裝

6. RBAC Model（完整資料庫 + PermissionService）

支援： • 多角色 • 多資源（Notebook / Note / Tag / Invitation） • 多操作（READ / CREATE / UPDATE / DELETE） • 以 resource_instance_id 進行細粒度控制

⸻

🧱 技術架構

Backend – Spring Boot • Java 21 + Spring Boot 3.x • Spring Security（自訂 JWT Filter + AuthorizationManager） • QueryDSL（複雜查詢） • PostgreSQL（主資料庫） • JPA（Entity 維護、關聯 Lazy） • Native SQL（RBAC 相關查詢） • Docker + Compose（開發環境）

Frontend – Nuxt • Nuxt 4（Vue 3） • BFF 層負責 API Proxy，前端不直接呼叫 BE • JWT 儲存在 BFF，瀏覽器僅持有 Session Cookie • UI 用 Nuxt UI + Tailwind

Architecture • MVVM 前端架構（View / ViewModel / Model-Composables） • BFF Proxy 層（sidebase/auth） • BE 採用分層 + Domain Package • controller • domain (entity, DTO, state machine, rbac, etc.) • service • repository • exception handlers • DDD 思維逐步導入（部分 Aggregate 設計）

⸻

🗃️ 資料庫模型（簡述）

Users

Notebooks

Notes

Tags

NoteTags（複合 PK）

Invitations（含 FSM 狀態）

RBAC Tables • roles • operations • resources • user_roles • role_permissions • resource_acl（細粒度 userId + resource_instance_id + roleId）

⸻

🚀 未來要開發的功能

以下是 Linknote 的第二階段與第三階段的願景，這也是未來要結合 RAG、資料庫強化、MCP 的地方：

⸻

✨ 1. RAG（Retrieval-Augmented Generation）整合

目的 • AI 依照使用者筆記內容，回答使用者問題 • 提供搜尋、推論、個人知識庫助理

計畫功能 • 文章向量 embedding • 筆記 / 高亮 / Tag 三層級的 semantic search • Retrieval Pipeline • Re-ranking • Context Construction

資料庫可能加入： • pgvector • 或外部向量 DB（Weaviate / Qdrant）

⸻

✨ 2. 共筆（Real-time Collaboration）

計畫技術 • CRDT（Yjs / Automerge） • WebSocket（Nuxt → BFF → Spring WebFlux） • 區塊級編輯權限（Paragraph locking） • 操作紀錄與版本控制

⸻

✨ 3. MCP（Model Context Protocol）整合

目標： • Linknote 成為 AI Agent 可以讀寫的「知識倉庫」 • MCP 工具可提供如下能力： • readNote • createNote • updateNotebook • searchNotes • embeddingSearch • 讓 AI 能協助建立筆記、分類、整理、連結

⸻

✨ 4. 自動化推薦系統（Hybrid Recommender） • Tag-based • Content-based（向量） • Graph-based（筆記關聯度） • User-based（共同閱讀/共同標記）

⸻

✨ 5. 高亮內容抽取（Highlight-based context） • 高亮即是使用者關注點 • 用高亮向量化建立「重要句子」排名模型 • 在 RAG 中優先檢索高亮內容

⸻

✔️ 文件目的

這份文件的用途包括： 1. 讓新開發者能快速理解 Linknote 2. 讓 AI Agent 能理解 project context 3. 作為未來 ADR 與技術決策的依據 4. 作為 BFF / RBAC / RAG / MCP 等文件的索引首頁

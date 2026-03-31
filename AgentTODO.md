# AgentTODO

## RAG 個人筆記助理

### FastAPI

- [x] `POST /ingest` 修改為接受 `Array<Note>` request body，支援新增與更新（先刪除 note_id 所有 chunks，再重新 embed 寫入，idempotent）
- [x] `DELETE /notes/{noteId}` 刪除 Qdrant 中指定 note_id 的所有 chunks
- [x] `POST /query` 加入 `user_id` payload filter，確保查詢結果隔離

Request body schema（`POST /ingest`）：

```json
{
    "notes": [
        {
            "note_id": "uuid",
            "user_id": "uuid",
            "notebook_id": "uuid",
            "title": "string",
            "question": "string | null",
            "content": "string | null",
            "keypoint": "string | null"
        }
    ]
}
```

Qdrant payload 欄位：`user_id`, `note_id`, `notebook_id`, `title`, `chunk_index`, `text`

Point ID：`uuid5(user_id:note_id:chunk_index)` — 不同使用者 ingest 同一筆記不互衝

---

### Spring Boot

- [x] 新增 `rag_notes` 資料表（`Database/schema.sql`）

```sql
CREATE TABLE rag_notes (
  note_id         UUID        PRIMARY KEY,
  user_id         UUID        NOT NULL,
  notebook_id     UUID        NOT NULL,
  note_updated_at TIMESTAMP   NOT NULL,
  created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

- [x] `POST /rag-notes` — upsert rag_note 紀錄（ON CONFLICT DO UPDATE）
- [x] `DELETE /rag-notes/{noteId}` — 從 rag_notes 移除紀錄
- [x] `GET /rag-notes?notebookId=` — 回傳 rag_notes 列表（含 note_updated_at）

---

### BFF（Nuxt server/api）

- [x] `POST /api/rag/ingest` — 向 Spring Boot 取得 notes → 送 FastAPI `/ingest` → 寫入 rag_notes
- [x] `DELETE /api/rag/notes/[noteId]` — 呼叫 FastAPI + Spring Boot 刪除
- [x] `GET /api/rag/notes` — 取得 rag_notes，與 notes 比對 updated_at 計算版本狀態

版本狀態邏輯：

- `up_to_date`：`note.updated_at <= rag_note.note_updated_at`
- `outdated`：`note.updated_at > rag_note.note_updated_at`，顯示落後天數
- `deleted`：note 已不存在但 rag_notes 還有紀錄

---

### 前端（Nuxt pages/components）

- [x] 筆記選擇頁面（`/rag`）— 選擇 notebook、勾選 notes、送出 ingest
- [x] RAG 狀態總覽表格 — 筆記名稱 / 筆記更新日期 / RAG 建立日期 / 狀態（up to date / 落後 N 天 / deleted）

---

## Invitation & 共編筆記本

### 階段一：Spring Boot 後端

- [x] `InvitationRepositoryImpl` — BASE_SELECT 加入 `JOIN notebooks nb` 和 `JOIN users u2 ON i.inviter_id = u2.id`，取得 `notebook_title`、`inviter_username`
- [x] `Invitation.java`（entity）— 新增 `String inviterName`、`String notebookTitle`
- [x] `InvitationRowMapper.java` — 映射 `inviter_username`、`notebook_title` 兩個新欄位
- [x] `InvitationDTO.java` — 新增 `UUID notebookId`、`String notebookTitle`、`String inviterName`；更新 `fromEntity()`
- [x] `InvitationServiceImpl.java` — 注入 `PermissionService`，ACCEPT 後呼叫 `addResourceRolePermission(inviteeId, notebookId, ROLE_COLLABORATOR, NOTEBOOK)`
- [x] `NotebookCondition.java` — 新增 `Boolean collab` 欄位
- [x] `NotebookRepositoryImpl.java` — `buildConditions()` 判斷 `collab == true` 時，改用 RBAC role_permissions 驗證 READ 權限，排除 ROLE_OWNER

### 階段二：前端串接

- [x] BFF `server/api/invitations/index.get.ts` — proxy `GET /invitations`（收到的邀請）
- [x] BFF `server/api/invitations/sent.get.ts` — proxy `GET /invitations/sent`（寄出的邀請）
- [x] BFF `server/api/invitations/index.post.ts` — proxy `POST /invitations`（建立邀請）
- [x] BFF `server/api/invitations/index.put.ts` — proxy `PUT /invitations`（accept/reject/resend）
- [x] BFF `server/api/invitations/index.delete.ts` — proxy `DELETE /invitations?invitationId=`
- [x] `composables/model/useInvitation.ts` — 新增 `indexReceived`、`indexSent`、`createInvitation`、`updateInvitation`、`deleteInvitation`
- [x] `pages/invitations.vue` — 兩個 UTable：收到的邀請（含 accept/reject 按鈕）、寄出的邀請（含刪除按鈕）
- [x] `pages/co-notebooks.vue` — 呼叫 `indexNotebook({ collab: true })`，NotebookCard 網格（只讀）

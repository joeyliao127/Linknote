# FE TODO

## Layout / 共用元件
- [ ] Dashboard layout：建立 `DashboardShell`（包裝 `UDashboardGroup`，slots: sidebar/header/main），供所有內頁套用。
- [ ] Sidebar：實作 `components/dashboard/Sidebar.vue`，含主導航、Notebook 手風琴列表（infinite scroll + loading）、快速建立入口。
- [ ] Notebook/Note cards：重寫 `components/ui/NoteCard.vue`（標題/星標/標籤/刪除動作）並新增 `NotebookCard.vue`（標題、描述、擁有者/共編標記、統計資訊、左下垃圾桶刪除按鈕、action slot）。
- [ ] Note toolbar：新增 `components/note/NoteToolbar.vue`，提供 create/all/star/tag/sort/keyword 按鈕（含 Tooltip）並以 emits 傳出查詢條件。
- [ ] NotebookForm：新增 `components/notebook/NotebookForm.vue`（title/description/active），供 /notebooks/create 及後續重用。

## Composables / 模型
- [ ] Notebook/Note/Tag composable 改接 Nuxt `/api`（BFF）並支援 query/pagination/filter（collab、keyword、tagIds、star、sort、page/pageSize）。
- [ ] 共用 `useInfiniteScroll`（IntersectionObserver + loading/hasMore）與 `useDashboardMock`（index 頁假資料）。
- [ ] Note detail composable：補 `getNoteDetail`、`updateNoteMeta`/`updateNoteContent` 等 API 包裝；若 BFF 缺少 GET /notes/{id} 則新增 handler。

## Pages
- [ ] `/signIn`：強化 UI（左側品牌/介紹 + 右側 SignIn/SignUp tabs）、表單 loading/錯誤訊息與禁用狀態。
- [ ] `/index`：Dashboard 概況頁；統計卡（notebooks/notes/分享數）+ 最近開啟筆記列表（可點擊跳 /notes/{noteId}），資料使用 `useDashboardMock`。
- [ ] `/notebooks`：搜尋 + collab filter + 分頁/無限滾動列表（NotebookCard 網格）；同步 query string `collab`、`keyword`；頁面只處理 composable 返回的資料不直接 fetch。
- [ ] `/notebooks/create`：套用 `NotebookForm`，提交後 toast + 導向列表或詳細。
- [ ] `/notebooks/{id}/notes`：Note list；工具列（create/all/star/tag/sort/keyword + Tooltip）綁定 query string，Card 顯示 title/star/tags/刪除按鈕、滾動載入追加 notes。
- [ ] `/notes/{id}`：LeetCode 風格兩欄；左側上/下可調整區塊，meta 區放標題/星標/標籤/問題/重點輸入，下區使用 `UEditor` 編輯，上區展示基本資訊；右側 markdown 預覽，含儲存/同步。
- [ ] `/settings`：基本設定表單（username/password 更新）+ 驗證與回饋訊息。

⏺ 這是個很好的問題。你的架構已經有 Clean Architecture 的影子（Repository interface、DTO、分層結構），但有幾個核心觀念還沒完整落地。

---

1. Anemic Domain Model（貧血模型）

目前的 Entity 只是資料容器：

// 現在：entity 只有 getter/setter note.setStar(true); note.setUpdatedAt(Instant.now());

// Clean Architecture 想要的：業務邏輯在 entity 裡 note.markAsStar(); // entity 內部處理 updatedAt

業務規則散落在 Service 層，entity 本身沒有行為，這是最常見的 Clean Architecture 缺失。

---

2. Use Case 層缺失

目前的層次：Controller → Service → Repository

Clean Architecture 的層次：Controller → Use Case → Domain Entity → Repository (port)

Service 目前混合了兩件事：

- Application logic（協調流程，例如 ingest 要先刪再寫）
- Domain logic（業務規則，例如筆記的星號邏輯）

這兩者應該分開，Use Case 負責流程，Domain Entity 負責規則。

---

3. 沒有測試

Clean Architecture 最重要的目的之一就是讓核心邏輯可以不依賴框架測試。

目前看不到任何測試檔案。如果 Use Case 層存在，你可以：

// 不需要 Spring、不需要 DB，直接測業務邏輯 @Test void should_mark_note_outdated_when_updated_after_ingest() { Note note = new Note(...); RagNote ragNote = new RagNote(...); assertTrue(ragNote.isOutdated(note)); }

沒有測試，架構再乾淨也只是信仰，不是保證。

---

4. Domain Event 缺失

當筆記被刪除時，Qdrant 的 chunks 和 rag_notes 的紀錄應該跟著清除。目前這個邏輯沒有實作（AgentTODO.md 裡也沒有），因為沒有觸發機制。

Clean Architecture 的解法：

Note.delete() → 發出 NoteDeletedEvent → RagNoteEventHandler 監聽 → 清除 rag_notes → QdrantEventHandler 監聽 → 清除 chunks

這樣各服務的清除邏輯解耦，NoteService 不需要知道 RAG 的存在。

---

5. Ports & Adapters 不完整

你有 Repository interface（這是 port 的概念），但外部服務沒有：

# FastAPI 目前：route 直接呼叫 ollama_embedder

embeddings = await embed(texts)

# 乾淨的做法：定義 port

class EmbeddingPort(ABC): async def embed(self, texts: list[str]) -> list[list[float]]: ...

class OllamaEmbeddingAdapter(EmbeddingPort): ...

好處是未來換掉 Ollama 換成 OpenAI，只需要換 Adapter，Use Case 不用動。

---

6. 錯誤處理邊界不清

目前有些地方拋 domain exception（NoteNotFoundException），有些地方直接用 HTTP status code，兩種風格混用。

Clean Architecture 的做法：

- Domain / Use Case：只拋 domain exception，不知道 HTTP 的存在
- Controller（最外層）：統一把 domain exception 轉成 HTTP response

---

總結

┌──────────────────────────────┬──────────────────────────────────────┐ │ 觀念 │ 現況 │ ├──────────────────────────────┼──────────────────────────────────────┤ │ Repository pattern │ ✅ 有，但部分 │ ├──────────────────────────────┼──────────────────────────────────────┤ │ Domain entity 有行為 │ ❌ 貧血模型 │ ├──────────────────────────────┼──────────────────────────────────────┤ │ Use Case 層 │ ❌ 混在 Service │ ├──────────────────────────────┼──────────────────────────────────────┤ │ 測試 │ ❌ 缺失 │ ├──────────────────────────────┼──────────────────────────────────────┤ │ Domain Event │ ❌ 缺失 │ ├──────────────────────────────┼──────────────────────────────────────┤ │ Ports & Adapters（外部服務） │ ⚠️ 部分（DB 有，Ollama/Qdrant 沒有） │ ├──────────────────────────────┼──────────────────────────────────────┤ │ 錯誤處理邊界 │ ⚠️ 不一致 │ └──────────────────────────────┴──────────────────────────────────────┘

最值得優先補的是測試和Use Case 層，這兩個一起做，會逼你把 domain logic 從 Service 裡抽出來，自然帶出其他改善。

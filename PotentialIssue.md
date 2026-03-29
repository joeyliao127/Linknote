# PotentialIssue

## [RAG] Qdrant payload 的 `text` 欄位空間問題

**描述：**
目前每個 Qdrant point 的 payload 都存有 `text`（原始 chunk 內容），方便 `/query` 直接回傳給前端，不需要再回查 DB。

**潛在問題：**
當筆記數量大、chunk 數量多時，payload 儲存的文字內容會顯著增加 Qdrant 的磁碟用量。Qdrant 的設計初衷是向量 + 少量 metadata，大量文字存 payload 並非最佳實踐。

**可能的解決方向：**
- 移除 payload 中的 `text`，`/query` 回傳 `note_id` + `chunk_index` 後，由 BFF 回查 PG 取得原始內容（多一次 I/O，但 Qdrant 更輕量）
- 保留現狀，等實際資料量到達瓶頸再處理

**目前決策：** 保留 `text` 於 payload，待規模成長後重新評估。

# AI Agent 協作流程規範（Codex / ChatGPT 等）

此文件用來說明我與 AI Coding Agent（以下簡稱「Agent」）協作時的 **指令格式** 與 **流程約定**，讓對話更穩定、可預期，也方便未來的我或其他人接手，

---

## 1. 主要指令狀態

-   #plan
-   #impl
-   #
-   #summary (總結，整理當日完成，在 worklog 中產生 yyyy-mm-dd.md 的文件)

所有和 Agent 的對話，建議以「標題 Tag」開頭，代表這次互動的目的與期待輸出。

### 1.1 `#plan`：請 Agent 先幫我想計劃

用途：

-   我有一個需求 / feature / refactor 想做，但**還不想直接寫 code**。
-   希望 Agent 先幫我拆解成步驟、決策點、風險。

格式示例：

```text
#plan

目標：
- 在 Linknote 的 notebooks API 上新增分頁機制

已知條件：
- 後端使用 Spring Boot + JPA
- 目前有 PageCommand / PageResponse 抽象
- 前端使用 Nuxt composable 進行呼叫
```

請你：

-   拆解出實作步驟（後端 / BFF / 前端）
-   標註哪些步驟「可以自動產生程式碼」、哪些需要我手動檢查
-   補上潛在風險與 rollback 策略

Agent 回覆預期是「計劃 / 步驟 / 決策說明」，不要直接貼一大坨 code（除非我在 #plan 明講可以）。

Agent 回覆預期是「計劃 / 步驟 / 決策說明」，不要直接貼一大坨 code（除非我在 #plan 明講可以）。

### 1.2 #impl：確認計劃後，請 Agent 協助實作

用途：

-   我已經有一份計劃（可能是上一個 #plan 的結果，或我自己寫的）。 • 想請 Agent 依據計劃產生程式碼、指令、或修改範例。
-   想請 Agent 依據計劃產生程式碼、指令、或修改範例。

格式示例：

```text

#impl

以下是已確認過的計劃（節錄）：
1. 在 BE 新增 Notebook 分頁查詢 API
2. 使用 JPA Page<T> + PageResponse<R> 做統一封裝
3. BFF 只轉發 page / pageSize 並維持型別

請依照上述計劃：
- 幫我產出 NotebookController 的 index 分頁版本範例
- 幫我產出對應的 BFF handler 範例（Nuxt server API）
- 幫我產出對應的 composable 呼叫範例

請重點放在程式碼，不需要再重講理論。
```

### 1.3 #replan：對計劃不滿意，請重新規劃

用途：

-   Agent 給的 #plan 不符合預期：太抽象、太複雜、跟現有架構不合、或忽略了既有約束。
-   我會說明「哪裡不 OK」，請 Agent 根據這些 feedback 重新設計計劃。

格式示例：

```text
#replan

上一版計劃的問題：
- 過度抽象，新增了太多額外的 Service / Manager，實務上我無法在一個週末內完成
- 沒有考慮到我現在的 RBAC 已經實作在 PermissionService / RBACAuthorizationManager 上
- 我希望「盡量在現有結構上微調」，而不是大翻修

請你重新幫我產生一份計劃，約束條件如下：
- 不能新增額外 microservice
- 必須沿用現有的 Spring Security + JWT 架構
- RBAC 查詢應該呼叫現有 PermissionService

請先只給「更新後的計劃」，不要給程式碼。
```

### 1.4 #log：總結今天完成的事項

-   用於記錄當日完成的事情，可以用 commit log 看今天完成的事項
-   撰寫隔日要完成的內容，便於隔日快速上手，如果你不知道隔日要完成哪些功能可以先詢問
-   產生 markdown 文件，紀錄於 /docs/worklog，檔名已當天日期 yyyy-mm-dd.md 命名
-   先條列式列出完成項目，我 review 後一樣會用 #impl 請你開始執行

```text
#log
```

## 2. 對話基本約定

1. 每個 major 任務都以 #plan 開頭
    - 除非是很小的問題（例如：「這個錯誤訊息是什麼意思」），否則先讓 Agent 幫忙整理思路是划算的。
2. 只有在計劃 OK 時才用 #impl 要程式碼
    - 避免「邊想架構邊寫 code」導致迷路。
3. 當我覺得計劃歪掉時，就用 #replan
    - 清楚說出我哪裡不滿意（過度工程 / 忽略現有限制 / 難以在時間內完成），讓 Agent 調整。
4. 我說明的 context 視為 Single Source of Truth
    - 若文件、程式碼、或 Agent 的記憶互相矛盾，以我在 prompt 中最新描述為主。
5. 盡量讓任務「小而可控」
    - #plan 也可以只針對一小段（例如：只針對 Invitation state machine 的 update 邏輯），減少一次改太多的風險。

## 3. 與專案文件的關係

-   架構決策（例如：為何使用 BFF, 為何採用 RBAC + ACL）
    -   寫在：docs/architecture/ArchitectureDecisionRecord.md
-   具體技術約定（例如：BFF 儲存 JWT, FE 只持有 session id, 分頁規則, 命名約定）
    -   寫在：docs/convention/
-   這份文件 (AI-Agent-Workflow.md)
    -   專門給「我自己 + Agent」看，
-   說明：我要 Agent 怎麼配合我
    -   說明：我會用哪些 Tag（#plan / #impl / #replan）來溝通

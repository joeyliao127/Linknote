# BFF

Nuxt Server 當作 BFF 的理由。

職責：

1. 儲存 JWT 內容
2. cache
3. 瀏覽器 interface

## 優點缺點概要

### 優點

1. 瀏覽器只存 Nuxt Server 的 session id 在 cookie (http only)
2. 個人資訊可在 Nuxt Server 提供 `/api/me` 取得後端存在 JWT 的 username，必免 userId 外露，也能讓 BFF 少打一次 fetch (類似 cache 的概念)
3. 修改後端 API 時 (不直接依賴後端)，UI 程式碼不用跟著修改，只需要調整 BFF 即可
4. 封裝 RBAC 權限，瀏覽器只知道能不能做，不知道是因為什麼權限才能做

### 缺點

1. 多一層 controller 要寫
2. 多一層 I/O 時間
3. cache 過期管理

## 細節說明

### 瀏覽器不直接接觸 JWT（降低被竊取風險）

JWT 若落在 client（localStorage / sessionStorage / memory）就可能被 XSS 竊取。 BFF 可把 JWT 安全地存於 server memory 或 HTTP-only cookie，避免前端直接暴露。

### 前端僅需要 Session Id，不需要 JWT

UI 不需要 userId 或任何敏感 claim，只需要 sessionId。這使資料暴露面積縮到最小。

### 統一的 API Proxy 流程更便於 RBAC 授權

所有 API 都經過 BFF，可以在 BFF 做： • attach JWT • attach resourceId • attach method → operation • 控制流量與日誌也讓 BE 的邏輯單純純粹。

### 封裝後端 API，避免網址暴露或被掃描

使用者永遠不會知道真正的 BE URL。潛在攻擊者無法直接 fuzz 掃 BE endpoint。

### 跨域（CORS）更容易管理

Nuxt → BFF → BE 瀏覽器永遠只 call Nuxt，不會產生複雜的 CORS 頭部設定問題。

### 減少前端重覆串接行為，統一輸出格式

BFF 可以把： • 多個 BE API 整合成一個 UI 專用 API • 回傳格式統一為 ApiResponse 這讓前端邏輯大幅簡化、也利於維護。

### 避免瀏覽器看到 RBAC 的細節

RBAC 相關資訊（role、operation、resource）不需傳到前端。前端永遠只知道「可訪問 / 不可訪問」。

### 更容易實作 SSR / SEO，需要使用者資訊

BFF SSR 時可直接讀取使用者的 sessionId 查詢使用者資訊，不需在前端做繁雜的 hydration。

### 可加入快取、壓力控管、Rate Limit

BFF 層是天然的 Gateway，可以放： • rate limit • caching • tracing • 錯誤統一處理而不必污染後端邏輯。

### 更好地分離 concern：前端開發不需理解 JWT、RBAC 安全細節

前端工程師只需處理 UI 與畫面邏輯。鑰匙（JWT）與權限（RBAC）都由 BFF + BE 高度一致地管理。

# Cold Boot Record

首次部署實際遇到的問題與解法，供下次參考。

## VM 架構

| VM | IP | 服務 |
|---|---|---|
| PostgreSQL | 192.168.0.202 | postgres:16 |
| Redis | 192.168.0.204 | redis:7 |
| Qdrant | 192.168.0.205 | qdrant:v1.17.1 |
| App | 192.168.0.206 | nuxt / springboot / fastapi |

---

## 測試各基礎服務是否正常

### PostgreSQL（從 Mac 測試，不需安裝 psql）

```bash
docker run --rm -it postgres:16 psql \
  -h 192.168.0.202 -p 5432 -U <user> -d linknote
```

看到 `linknote=#` 即成功。

> PostgreSQL 用 Docker 跑時預設會監聽所有 IP，port 透過 `ports` 對外暴露，不需額外設定。

### Redis

```bash
docker run --rm -it redis:7 redis-cli \
  -h 192.168.0.204 -p 6379 -a <password> ping
```

回傳 `PONG` 即成功。

### Qdrant

```bash
curl -H "api-key: <QDRANT_API_KEY>" \
  http://192.168.0.205:6333/collections
```

或直接開瀏覽器：`http://192.168.0.205:6333/dashboard`

---

## 遇到的問題

### 1. Redis container 持續重啟

**症狀：** `docker ps` 顯示 `Restarting (1)`，container 無法維持運作。

**原因：** `docker-compose-redis.yaml` 的 command 為：
```yaml
command: redis-server --requirepass ${REDIS_PASSWORD}
```
`REDIS_PASSWORD` 為空字串時，Redis 不接受空密碼，啟動失敗。

**解法：** `.env.prod` 中務必填入 `REDIS_PASSWORD`，不可留空。

---

### 2. Qdrant client 版本不相容

**症狀：**
```
UserWarning: Qdrant client version 1.17.1 is incompatible with server version 1.15.4.
```

**原因：** qdrant_client（Python 套件）與 server image 版本差距超過 1 個 minor version。

**解法：** 將 `docker-compose-qdrant.yaml` 的 image 釘至對應版本：
```yaml
image: qdrant/qdrant:v1.17.1
```

---

### 3. Qdrant API Key 導致 502（HTTPS/HTTP 不相符）

**症狀：**
```
UserWarning: Failed to obtain server version. Unable to check client-server compatibility.
DELETE /notes/{id} HTTP/1.1" 502 Bad Gateway
```

Qdrant server log：
```
actix_http::h1::dispatcher: stream error: request parse error: invalid Header provided
```

**原因：** `qdrant_client` 只要傳入 `api_key`，就會自動將連線切換為 HTTPS。但 server 是 HTTP，導致 server 收到 TLS handshake 而解析失敗。

**解法：** 建立 client 時明確指定 `https=False`：
```python
AsyncQdrantClient(
    host=settings.QDRANT_HOST,
    port=settings.QDRANT_PORT,
    api_key=settings.QDRANT_API_KEY or None,
    https=False,
)
```

---

### 4. Nuxt Redis 連線設定錯誤

**症狀：** Production 環境 Nuxt 無法連到 Redis VM。

**原因：** `nuxt.config.ts` 的 Redis host 寫死為容器名稱 `"redis"`，且未帶入密碼：
```ts
storage: {
    redis: {
        driver: "redis",
        host: "redis",   // 寫死，production 無效
        port: redisPort,
        // 沒有 password
    }
}
```

**解法：** 改為讀取環境變數：
```ts
storage: {
    redis: {
        driver: "redis",
        host: redisHost,      // process.env.REDIS_HOST || "redis"
        port: redisPort,
        db: redisDb,
        username: redisUsername,
        password: redisPassword,
    }
}
```

本地 dev 環境 `REDIS_HOST` 未設定時 fallback 為 `"redis"`（容器名稱），行為不變。

---

## 啟動順序

1. **基礎服務 VM 先啟動**（PG → Redis → Qdrant）
2. **App VM 後啟動**（SpringBoot → FastAPI → Nuxt）

App 服務依賴基礎服務，順序錯誤會導致啟動失敗或連線錯誤。

# Deployment Guide

## 概覽

```
本機 build image → push 到 GHCR → VM pull image → docker compose up
```

---

## 一、本機設定（第一次）

### 1. 建立 GitHub PAT

1. 前往 `https://github.com/settings/tokens/new`
2. Note 填入任意名稱（例如 `ghcr-push`）
3. 勾選 `write:packages`
4. 按 **Generate token**，複製並妥善保存

### 2. 登入 GHCR

```bash
echo "你的PAT" | docker login ghcr.io -u joeyliao127 --password-stdin
# 看到 Login Succeeded 即完成
```

### 3. 確認 buildx 可用

```bash
docker buildx version
```

---

## 二、Build & Push Image

在專案根目錄執行（一次 build 三個服務）：

```bash
./Docker/build/build-all.sh
```

或個別 build：

```bash
./Docker/build/springboot/build.sh
./Docker/build/nuxt/build.sh
./Docker/build/fastapi/build.sh
```

build 完成後可至 `https://github.com/joeyliao127?tab=packages` 確認 image 是否上傳成功。

> **注意**：本機為 Apple Silicon（arm64），build 時已指定 `--platform linux/amd64`，確保與 VM 相容。

---

## 三、VM（Ubuntu）設定（第一次）

### 1. 安裝 Docker 與 jq

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-plugin jq
```

### 2. 將使用者加入 docker 群組（免 sudo）

```bash
sudo usermod -aG docker $USER
```

**重新 SSH 連線**後生效。

### 3. 登入 GHCR

若 package 為 **private**，VM 也需要登入（使用有 `read:packages` 權限的 PAT）：

```bash
echo "你的PAT" | docker login ghcr.io -u joeyliao127 --password-stdin
```

若 package 已設為 **public** 則跳過此步。

### 4. Clone 專案

```bash
git clone git@github.com:joeyliao127/Linknote.git
cd Linknote
```

---

## 四、VM 環境設定

所有服務的設定值集中在 **一個檔案** `Docker/build/.env.prod`。

### 1. 填寫 `.env.prod`

```bash
nano Docker/build/.env.prod
```

需要填入的欄位：

| 欄位 | 說明 |
|---|---|
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | 資料庫帳號密碼 |
| `REDIS_PASSWORD` | Redis 密碼 |
| `QDRANT_API_KEY` | `openssl rand -hex 32` 產生 |
| `JWT_SECRET` | `openssl rand -hex 32` 產生 |
| `AUTH_SECRET` | `openssl rand -hex 32` 產生 |
| `AUTH_ORIGIN` | 實際 domain（例如 `https://linknote.example.com`）|
| `OPENAPI_EMBEDDING` | OpenAI API Key（使用 OpenAI embedding 時）|

### 2. 產生各服務 `.env`

```bash
chmod +x Docker/build/env-generator.sh
bash Docker/build/env-generator.sh
```

script 會讀取每個服務目錄下的 `key.json`，從 `.env.prod` 擷取對應的值，自動在各服務目錄產生 `.env`。

> **注意**：`.env.prod` 與 `.env` 皆已加入 `.gitignore`，不會 commit 到版本庫。

---

## 五、VM 啟動服務

### 1. 啟動基礎設施服務（各自在對應 VM 上執行）

**PostgreSQL VM（192.168.0.202）：**
```bash
cd Docker/build/postgresql
docker compose -f docker-compose-postgresql.yaml up -d
```

**Redis VM（192.168.0.204）：**
```bash
cd Docker/build/redis
docker compose -f docker-compose-redis.yaml up -d
```

**Qdrant VM（192.168.0.205）：**
```bash
cd Docker/build/qdrant
docker compose -f docker-compose-qdrant.yaml up -d
```

### 2. 啟動 App 服務（192.168.0.206）

Pull 最新 image：

```bash
docker pull ghcr.io/joeyliao127/linknote/springboot:latest
docker pull ghcr.io/joeyliao127/linknote/nuxt:latest
docker pull ghcr.io/joeyliao127/linknote/fastapi:latest
```

啟動所有 app 服務（nuxt / springboot / fastapi / nginx 共用同一網路）：

```bash
cd Docker/build
docker compose -f docker-compose-app.yaml up -d
```

### 3. 確認服務狀態

```bash
docker ps
```

---

## 六、更新部署

每次有新版本要部署時：

**本機：**
```bash
./Docker/build/build-all.sh
```

**VM（192.168.0.206）：**
```bash
docker pull ghcr.io/joeyliao127/linknote/springboot:latest
docker pull ghcr.io/joeyliao127/linknote/nuxt:latest
docker pull ghcr.io/joeyliao127/linknote/fastapi:latest

cd Docker/build
docker compose -f docker-compose-app.yaml up -d --force-recreate
```

---
name: skill-penguin
description: "LinkNote 專案快速 scaffolding 工具。當需要新增 Spring Boot entity/repository/service/controller 或 Nuxt BFF API routes 和 MVVM model 時使用。"
disable-model-invocation: true
allowed-tools: Bash, Read, Edit
---

# Skill Penguin — LinkNote Scaffolding

用於快速建立 Spring Boot 後端結構 和 Nuxt BFF 前端結構的腳本工具。
所有腳本均位於 `.codex/skills/skill-penguin/scripts/`，模板位於 `.codex/skills/skill-penguin/assets/be-template/`。

根據 `$ARGUMENTS` 決定執行哪個腳本：

---

## 1. `be-entity` — Spring Boot 後端 scaffolding

**觸發條件：** 使用者說「新增 BE entity」、「建立後端結構」、「scaffold [EntityName]」

**執行：**
```bash
node .codex/skills/skill-penguin/scripts/create-be-entity.mjs \
  --project-root BE/SpringBoot \
  --entity <EntityName> \
  --all
```

**產生的檔案：**
- `entity/<Entity>.java`
- `repository/<Entity>Repository.java` + `impl/` + `rowmapper/`
- `service/<Entity>Service.java` + `impl/`
- `controller/<Entity>Controller.java`
- `domain/<entity>/` — DTO, CreateCommand, UpdateCommand, Condition, OrderBy, Exception 系列

**Post-steps（必做）：**
1. 找出所有生成的 `.java` 檔案中的 `// TODO` 註解並補齊：
   - `Entity.java` — 補上 DB table 欄位（對應 `Database/schema.sql`）
   - `DTO.java` — 加入對應欄位，更新 `fromEntity()` mapper
   - `RowMapper.java` — 補上 `rs.get*()` 欄位對映
   - `CreateCommand.java` / `UpdateCommand.java` — 加入 request 欄位與 validation annotation
   - `Condition.java` — 視需要加入查詢過濾欄位
   - `OrderBy.java` — 視需要加入可排序欄位
2. 確認 `RepositoryImpl.java` 的 SQL 語句欄位與 DB schema 一致

---

## 2. `bff-api` — Nuxt BFF API Routes scaffolding

**觸發條件：** 使用者說「新增 BFF API」、「建立 server/api routes」

**執行：**
```bash
node .codex/skills/skill-penguin/scripts/create-bff-api.mjs \
  --project-root FE/Nuxt \
  --resource <resource-name> \
  --methods GET,POST,PUT,DELETE
```

**產生的檔案：**
- `server/api/<resource>/index.get.ts`
- `server/api/<resource>/index.post.ts`
- `server/api/<resource>/[id]/index.put.ts`
- `server/api/<resource>/[id]/index.delete.ts`
- `server/api/<resource>/[...resource].ts` — 403 catch-all

**Post-steps：**
1. 確認每個 route 檔案中的 backend API URL 正確（`config.RESOURCE_API`）
2. 視需要調整 query / body 參數名稱

---

## 3. `bff-model` — Nuxt MVVM Model scaffolding

**觸發條件：** 使用者說「新增 BFF model」、「建立 composable」、「新增 TypeScript types」

**執行：**
```bash
node .codex/skills/skill-penguin/scripts/create-bff-model.mjs \
  --project-root FE/Nuxt \
  --model <ModelName>
```

**產生的檔案：**
- `app/composables/model/use<Model>.ts` — 包含 index / create / update / delete 方法
- `types/<Model>.d.ts` — TypeScript interface + CreateDTO / UpdateDTO

**Post-steps：**
1. 在 `types/<Model>.d.ts` 中補上對應 DB 欄位
2. 確認 `use<Model>.ts` 中的 API endpoint 路徑正確

---

## 執行流程

1. 從 `$ARGUMENTS` 解析出：要執行哪個腳本、entity/resource/model 名稱、選用的 `--methods`
2. 用 Bash 執行對應腳本
3. 列出所有產生的檔案路徑
4. 依序處理 post-steps 中的 `// TODO` 項目（若使用者有提供欄位資訊則立即填入，否則列出待辦清單）

## 範例用法

```
/skill-penguin be-entity Notebook
/skill-penguin bff-api notebooks GET,POST,PUT,DELETE
/skill-penguin bff-model Notebook
/skill-penguin be-entity Invitation --skip-format
```

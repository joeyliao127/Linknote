---
name: skill-penguin
description: "Automate LinkNote scaffolding for Spring Boot and Nuxt BFF. Use when generating BE entity/repository/service/controller code or BFF MVVM models and API routes (interactive or CLI args)."
---

# Skill Penguin

## Scope
- Generate Spring Boot scaffolding (entity/repository/service/controller) from templates.
- Generate Nuxt BFF API routes (server/api) and MVVM model composables + types.
- Support interactive prompts or CLI args for non-interactive runs.

## Scripts
### create-be-entity
Location: `.codex/skills/skill-penguin/scripts/create-be-entity.mjs`

Use to scaffold Spring Boot files from templates in `assets/be-template`.

Interactive:
- `node .codex/skills/skill-penguin/scripts/create-be-entity.mjs`

Args (repo root):
- `node .codex/skills/skill-penguin/scripts/create-be-entity.mjs --project-root BE/SpringBoot --entity Notebook --types repository,service,controller`

Options:
- `--project-root <path>`: Spring Boot root (must contain `pom.xml`).
- `--template-root <path>`: override template source.
- `--types repository,service,controller` or `--all`: choose scaffolds.
- `--overwrite`: overwrite existing files without prompt.
- `--install-prettier`: auto-install prettier if missing.
- `--skip-format`: skip formatting.
- `--non-interactive`: fail fast when required inputs are missing.

Post-steps (required):
- Find all `TODO` comments in generated `.java` files and update them.
- 반드시 `DTO`, `Entity`, `Mapper` 三個檔案的欄位要與 DB 欄位一致。

### create-bff-api
Location: `.codex/skills/skill-penguin/scripts/create-bff-api.mjs`

Use to scaffold Nuxt BFF API routes in `server/api/`.

Interactive:
- `node .codex/skills/skill-penguin/scripts/create-bff-api.mjs`

Args (repo root):
- `node .codex/skills/skill-penguin/scripts/create-bff-api.mjs --project-root FE/Nuxt --resource notebooks --methods GET,POST,PUT,DELETE`

Options:
- `--project-root <path>`: Nuxt root (must contain `nuxt.config.ts`).
- `--resource <name>`: API resource name (e.g. notebooks).
- `--methods GET,POST,PUT,DELETE` or `--all`.

### create-bff-model
Location: `.codex/skills/skill-penguin/scripts/create-bff-model.mjs`

Use to scaffold MVVM model composable + type definitions.

Interactive:
- `node .codex/skills/skill-penguin/scripts/create-bff-model.mjs`

Args (repo root):
- `node .codex/skills/skill-penguin/scripts/create-bff-model.mjs --project-root FE/Nuxt --model Notebook`

Options:
- `--project-root <path>`: Nuxt root (must contain `nuxt.config.ts`).
- `--model <name>`: model name in PascalCase or camelCase (e.g. Notebook, notebook).

## Assets
- `assets/be-template/`: Spring Boot sample templates used by `create-be-entity`.

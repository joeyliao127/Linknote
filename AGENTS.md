# Repository Guidelines

## Project Structure & Module Organization
- `FE/Nuxt/`: Nuxt 4 frontend (SSR + BFF). Key areas: `app/`, `server/`, `shared/`, `public/`.
- `BE/SpringBoot/`: Spring Boot backend. Source in `src/main/`, tests in `src/test/`.
- `Database/`: local Postgres data volume (used by Docker).
- `Docker/`: docker-compose files and nginx config.
- `docs/`: architecture docs, including ADRs (see `docs/architecture/ArchitectureDecisionRecord.md`).

## Build, Test, and Development Commands
Frontend (from `FE/Nuxt/`):
- `pnpm dev`: run Nuxt dev server.
- `pnpm build_production` / `pnpm build_staging` / `pnpm build_testing`: build for target env.
- `pnpm preview`: preview a production build locally.
- `pnpm openapi:types`: regenerate `types/api.d.ts` from Spring Boot OpenAPI.

Backend (from `BE/SpringBoot/`):
- `./mvnw clean package -DskipTests`: build JAR.
- `./mvnw test`: run backend tests.
- `make build` / `make up` / `make re`: build JAR and restart the app container (see `Makefile`).

Docker (from repo root):
- `docker-compose -f Docker/docker-compose-services.yaml up -d`: start Postgres + Redis.
- `docker-compose -f Docker/docker-compose-app.yaml up -d`: start Spring Boot + Nuxt + nginx.

## Coding Style & Naming Conventions
- Follow existing 4-space indentation and double-quote strings in TypeScript files.
- Java follows standard Spring Boot conventions (package `com.penguin.linknote`, class names in `PascalCase`).
- Use descriptive names aligned with domain terms (Notebook, Note, Tag, RBAC).

## Testing Guidelines
- Backend tests use JUnit (see `BE/SpringBoot/src/test/`). Run with `./mvnw test`.
- No frontend test runner is configured; add tests only if you also introduce tooling.

## Commit & Pull Request Guidelines
- Commit messages follow `type: description` (e.g., `docs: update README.md`, `refactor: tag repository`).
- PRs should include a brief description, scope of changes, and any relevant screenshots for UI work.

## Configuration & Secrets
- Local config is centralized in `Docker/.env`. Update this for ports, credentials, and service hosts.
- Do not commit real secrets; use placeholders where needed.

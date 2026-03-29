# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LinkNote is a full-stack collaborative note-taking application with:
- **BE/** — Spring Boot 3.3.5 (Java 21) REST API
- **FE/** — Nuxt 4 frontend with BFF (Backend For Frontend) pattern
- **Database/** — PostgreSQL schema and seed data
- **Docker/** — Docker Compose for local dev and production

## Development Commands

### Start Infrastructure (required before running either app)
```bash
cd Docker
docker-compose -f docker-compose-services.yaml up -d   # PostgreSQL (5432) + Redis (6379)
```

### Frontend (Nuxt 4)
```bash
cd FE/Nuxt
pnpm install
pnpm dev                  # Dev server at http://linknote.local.com:3000
pnpm build_production     # Production build
pnpm build_testing        # Testing build
pnpm openapi:types        # Regenerate TS types from Spring Boot OpenAPI spec
```

### Backend (Spring Boot)
```bash
cd BE/SpringBoot
mvn clean install         # Build
mvn spring-boot:run       # Run (port 8080)
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Full Stack via Docker
```bash
cd Docker
bash startup.sh           # Starts both docker-compose files (infra + app)
```

## Architecture

### Request Flow
```
Browser → Nginx (80/443) → Nuxt BFF (3000, server/api/) → Spring Boot (8080) → PostgreSQL (5432)
                                     ↓
                               Redis (6379, sessions)
```

### BFF Pattern (Critical Design Decision)
The Nuxt `server/api/` directory acts as a secure gateway — **JWT tokens are stored server-side in the BFF, never in the browser**. The browser only receives an HTTP-only session cookie. This eliminates XSS attack vectors on tokens. Frontend page components call `/api/*` routes; those routes proxy to Spring Boot with the JWT attached server-side.

### Backend Structure (DDD-lite)
```
BE/SpringBoot/src/main/java/com/penguin/linknote/
├── domain/          # Business logic by feature (auth, user, note, notebook, tag, invitation, rbac, roles, resources, accesspolicies)
├── controller/      # REST endpoints
├── service/         # Application services
├── repository/      # Data access (NamedParameterJdbcTemplate, NOT JPA)
├── entity/          # Domain entities
├── config/          # Spring configurations (Security, JWT filter chain)
└── common/          # Shared utilities (ApiResponse, exceptions)
```

**Important:** The project uses raw JDBC (`NamedParameterJdbcTemplate`) for all DB queries — not JPA or QueryDSL. This was an intentional decision (ADR-011) for complex query control.

### Frontend Structure
```
FE/Nuxt/
├── app/
│   ├── pages/       # Route pages
│   ├── components/  # Vue components
│   ├── composables/ # Reusable composition functions
│   └── utils/       # Utilities
└── server/
    ├── api/         # BFF endpoints (proxies to Spring Boot)
    └── middleware/  # Server middleware
```

### Authorization Model (RBAC + Resource ACL)
4 roles with hierarchical permissions: `ROLE_OWNER` > `ROLE_COLLABORATOR` > `ROLE_MEMBER` > `ROLE_GUEST`

Resources: `NOTEBOOK`, `NOTE`, `TAG`, `COLLABORATOR`, `INVITATION`
Operations: `CREATE`, `DELETE`, `READ`, `UPDATE`

Two-layer auth:
1. **Role permissions** — `role_permissions` table maps role → resource → operations
2. **Resource ACL** — `resource_acl` table maps user + role + specific resource instance (fine-grained per-notebook/note access)

### Key Database Tables
- `users`, `notebooks`, `notes`, `tags`, `note_tags` — core domain
- `roles`, `operations`, `resources`, `role_permissions`, `resource_acl` — RBAC
- `invitations`, `notebook_user_roles` — collaboration

## Environment Configuration
Copy `Docker/.template.env` to `Docker/.env` and configure service IPs/ports. The `.env` is gitignored.

## Test Account
Email: `test@test.com` / Password: `abc123`

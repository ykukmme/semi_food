# AGENTS.md

## Project

Semi is a Spring Boot backend for a smart food e-commerce platform. It uses Java 21, Gradle, Spring Security, JWT, JPA, Flyway, and MySQL/TiDB-compatible storage.

## Hard Rules

- Do not hardcode secrets. Database URLs, API keys, JWT secrets, tokens, and credentials must come from environment variables or the ignored `.env` file.
- Keep `.env` out of Git. Update `.env.example` with placeholders only when new configuration keys are added.
- Validate user input at API boundaries with Bean Validation and `@Valid` where applicable.
- Keep schema changes in Flyway migrations under `src/main/resources/db/migration`; do not rely on Hibernate DDL generation.
- Keep auto-ordering and RPA-style automation opt-in. Risky automation must default to off and require an explicit flag.
- Add or update tests for behavior changes, especially auth, roles, JWT, and database migration behavior.

## Secrets Policy

- Never print, log, paste, or commit `.env` contents.
- Required local secrets live in `.env`; example keys live in `.env.example` without real values.
- `JWT_SECRET` must be at least 32 bytes.
- Production must set `CORS_ALLOWED_ORIGINS` to real domains or IP origins. Do not use `*` in production.

## Local Run

- Gradle: `./gradlew bootRun`
- VS Code F5: use the shared `SemiApplication` launch configuration.
- Browser entry points: `http://localhost:8080/login.html` and `http://localhost:8080/register.html`
- Tests: `./gradlew test`

Spring Boot imports `.env` through `spring.config.import`, so both Gradle `bootRun` and IDE main-class launches can use the same local config.

## Code Review Graph

This project has a code-review-graph knowledge graph. Try the graph tools before broad file scanning when exploring architecture, reviewing changes, or tracing impact.

Use file scanning as a fallback if graph tools time out or do not cover the needed context.

Recommended graph tools:

- `detect_changes` for code review.
- `get_review_context` for review snippets.
- `get_impact_radius` for blast radius.
- `get_affected_flows` for impacted execution paths.
- `query_graph` for callers, callees, imports, and tests.
- `semantic_search_nodes` for finding functions/classes.
- `get_architecture_overview` for high-level structure.

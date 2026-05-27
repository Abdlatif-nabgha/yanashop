# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**YanaShop** is a cloud-native e-commerce platform built as Java/Spring Boot microservices, targeting Kafka, Kubernetes, Terraform, and AWS. It uses a multi-module Maven structure with a parent POM coordinating all services.

Planned services (parent POM comments):
- `catalogue-service` — Product catalog bounded context (implemented)
- `order-service` — (coming)
- `api-gateway` — (coming)

## Build & Run Commands

All Maven commands should be run from the repo root unless targeting a specific module.

```bash
# Build all modules
./mvnw clean package -DskipTests

# Build a single service
./mvnw clean package -DskipTests -pl catalogue-service

# Run all tests (requires Docker for Testcontainers)
./mvnw verify

# Run tests for a single service
./mvnw verify -pl catalogue-service

# Run a single test class
./mvnw test -pl catalogue-service -Dtest=CatalogueServiceApplicationTests

# Run a specific service locally (after building)
java -jar catalogue-service/target/catalogue-service-1.0.0-SNAPSHOT.jar
```

## Infrastructure

Start the local database stack before running any service:

```bash
docker compose up -d
```

This starts:
- **catalogue-db**: PostgreSQL 16 on `localhost:5432`, database `catalogue_db`, user `nabgha`

catalogue-service listens on **port 8081**. Actuator endpoints are exposed at `/actuator/health`, `/actuator/info`, and `/actuator/metrics`.

## Architecture & Key Conventions

### Domain-Driven Design
The codebase follows DDD principles. Each service is a bounded context with its own database. Domain objects live under `src/main/java/com/nabgha/<service>/domain/`:
- **Value objects** are immutable `final` classes with private constructors and factory methods (e.g., `ProductId` wraps a `UUID`).
- IDs use `UUID` as the underlying type.

### Database Schema Management
- Schema is **exclusively managed by Flyway** — `ddl-auto` is set to `validate`, so Hibernate never modifies the schema.
- Migrations live in `src/main/resources/db/migration/` and follow the naming convention `V{n}__{description}.sql`.
- New database changes must be added as new versioned migration files, never by editing existing ones.

### Testing
- Integration tests use **Testcontainers** (PostgreSQL container) — no external database is needed at test time.
- Unit tests (Surefire) run during `test` phase; integration tests (Failsafe) run during `verify`.

### Package Structure (per service)
```
com.nabgha.<service>/
  domain/
    model/       # Value objects, entities, aggregates
    repository/  # Repository interfaces (ports)
    service/     # Domain services
  application/   # Use-case / application services
  infrastructure/
    persistence/ # JPA entities, repository implementations
    web/         # REST controllers, DTOs
```
*(Only `domain/model` is implemented so far; this is the intended structure.)*

### Parent POM Responsibilities
The parent `pom.xml` owns all **dependency versions** via BOMs (Spring Boot, Spring Cloud, Testcontainers). Child modules declare dependencies without versions. Plugin versions are also managed in the parent's `pluginManagement`.

## Adding a New Service

1. Create a new directory (e.g., `order-service/`) with its own `pom.xml` inheriting from the parent.
2. Register it in the parent `pom.xml` `<modules>` section.
3. Add a dedicated database service to `docker-compose.yml`.
4. Place Flyway migrations under `src/main/resources/db/migration/`.
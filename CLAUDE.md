# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**YanaShop** is a cloud-native e-commerce platform built as Java 21 / Spring Boot 3.5 microservices, targeting Kafka, Kubernetes, Terraform, and AWS. It uses a multi-module Maven structure with a parent POM coordinating all services.

Modules:
- `catalogue-service` — Product catalog bounded context (implemented)
- `order-service` — (planned)
- `api-gateway` — (planned)

## Build & Run Commands

All Maven commands should be run from the repo root unless targeting a specific module.

```bash
# Build all modules
./mvnw clean package -DskipTests

# Build a single service
./mvnw clean package -DskipTests -pl catalogue-service

# Run unit tests only (no Docker needed)
./mvnw test -pl catalogue-service

# Run unit + integration tests (requires Docker for Testcontainers)
./mvnw verify -pl catalogue-service

# Run a specific test class
./mvnw test -pl catalogue-service -Dtest=MoneyTest

# Run a specific integration test class
./mvnw verify -pl catalogue-service -Dit.test=ProductControllerIT

# Run a specific service locally (after building)
java -jar catalogue-service/target/catalogue-service-1.0.0-SNAPSHOT.jar
```

**Test file naming:** unit tests end in `*Test.java` (run by Surefire during `test`), integration tests end in `*IT.java` (run by Failsafe during `verify`).

## Infrastructure

Start the local database stack before running any service:

```bash
docker compose up -d
```

This starts:
- **catalogue-db**: PostgreSQL 16 on `localhost:5432`, database `catalogue_db`, user `nabgha`
- **catalogue-service**: built from `catalogue-service/Dockerfile` on port `8081`

`catalogue-service` listens on **port 8081**. Actuator endpoints: `/actuator/health`, `/actuator/info`, `/actuator/metrics`.

**Docker build:** The `Dockerfile` is at `catalogue-service/Dockerfile` but the build context must be the **repo root** (so it can copy the parent `pom.xml`). Always run `docker compose` or `docker build` from the repo root.

## Architecture & Key Conventions

### Hexagonal Architecture (Ports & Adapters)

Each service is a bounded context following hexagonal architecture. The `domain` package is fully isolated from Spring — it has no framework dependencies.

**Ports:**
- `domain/port/in/` — inbound use-case interfaces (e.g., `CreateProductUseCase`, `GetProductUseCase`). Each has a single method `execute(...)` or descriptively named method.
- `domain/port/out/` — outbound repository interface (e.g., `ProductRepository`).

**Adapters:**
- `application/service/ProductApplicationService` — implements *all* inbound use-case interfaces in one class; injected via `BeanConfig`.
- `infrastructure/persistence/PostgresProductRepository` — implements `ProductRepository` using Spring Data JPA (`SpringDataProductRepository`) + a `ProductDbMapper`.
- `infrastructure/web/ProductController` — injects use-case interfaces directly (not the application service class). All REST responses are wrapped in `ApiResponse<T>` with `success`, `message`, and `data` fields.

### Domain Model

- **Value objects** are immutable `final` classes with private constructors and factory methods (`Money`, `ProductId`, `Category`).
- **`Product`** is an entity with identity-based equality. It exposes mutation methods (`rename`, `changePriceTo`, `decreaseStock`, etc.) that enforce invariants — never set fields directly.
- Two factory methods: `Product.create(...)` generates a new `ProductId`; `Product.restore(...)` is used only by `ProductDbMapper` when loading from DB.

### Database Schema Management
- Schema is **exclusively managed by Flyway** — `ddl-auto` is `validate`, so Hibernate never modifies the schema.
- Migrations live in `src/main/resources/db/migration/` with the naming convention `V{n}__{description}.sql`.
- New database changes must be added as new versioned migration files, never by editing existing ones.

### Testing
- Integration tests extend `AbstractIntegrationTest`, which starts a PostgreSQL 16 Testcontainers container and wires it via `@ServiceConnection`. No external database is needed at test time.

### Package Structure (per service)
```
com.nabgha.<service>/
  domain/
    model/        # Value objects, entities, aggregates
    port/
      in/         # Inbound use-case interfaces + command records
      out/        # Outbound repository interface
    exception/    # Domain exceptions
  application/
    service/      # ApplicationService implementing all use-case interfaces
  infrastructure/
    config/       # Spring @Bean wiring (BeanConfig)
    persistence/  # JPA entity, Spring Data repo, mapper, adapter
    web/          # REST controller, DTOs, mappers, GlobalExceptionHandler
```

### Parent POM Responsibilities
The parent `pom.xml` owns all **dependency versions** via BOMs (Spring Boot 3.5, Spring Cloud 2025.0, Testcontainers 1.20). Child modules declare dependencies without versions. Plugin versions (`maven-compiler-plugin`, `maven-surefire-plugin`, `maven-failsafe-plugin`, `spring-boot-maven-plugin`) are also managed in the parent's `pluginManagement`.

## Kubernetes

Manifests live in `k8s/` and are numbered for apply order. They target the `yanashop` namespace:

```bash
kubectl apply -f k8s/
```

Current manifests deploy PostgreSQL with a PVC, ConfigMap, and Secret. The `catalogue-service` deployment manifest is not yet present.

## CI/CD

GitLab CI (`.gitlab-ci.yml`) uses `maven:3.9.6-eclipse-temurin-21`. Stages:
1. **build** — `mvn clean compile`
2. **test** — `mvn test` (unit tests only, no Docker)
3. **integration-test** — currently commented out (requires Docker-in-Docker)

## Adding a New Service

1. Create a new directory (e.g., `order-service/`) with its own `pom.xml` inheriting from the parent.
2. Register it in the parent `pom.xml` `<modules>` section.
3. Add a dedicated database service to `docker-compose.yml`.
4. Place Flyway migrations under `src/main/resources/db/migration/`.
5. Follow the hexagonal package structure above; wire use cases in a `BeanConfig` class.
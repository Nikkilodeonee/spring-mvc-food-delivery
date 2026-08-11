# Food Delivery REST API — Spring MVC Multi-Module

[![Build and Test](https://github.com/Nikkilodeonee/spring-mvc-food-delivery/actions/workflows/build.yml/badge.svg)](https://github.com/Nikkilodeonee/spring-mvc-food-delivery/actions/workflows/build.yml)

A production-style multi-module Spring Boot REST API for food ordering.  
The project demonstrates layered backend architecture, secured endpoints, database migrations, containerized PostgreSQL setup, and integration testing with Testcontainers.

## Highlights

- Production-style multi-module Spring Boot REST API
- PostgreSQL + Flyway migrations
- Docker Compose local environment
- GitHub Actions CI
- MockMvc and Testcontainers tests
- Spring Security authentication
- Layered architecture with controllers, services, repositories

## Project purpose

This project demonstrates how I structure a real backend service with clear module boundaries, API contracts, persistence, business rules, security, database migrations, Docker-based local infrastructure, and CI-tested PostgreSQL integration tests.

## Architecture

```text
api          → OpenAPI spec and generated DTOs
persistence  → JPA entities and repositories
service      → transactional business logic
web          → REST controllers, security, exception handling
```

```mermaid
flowchart LR
    Client --> Web
    Web --> Service
    Service --> Persistence
    Persistence --> PostgreSQL[(PostgreSQL)]
    Flyway --> PostgreSQL
```

## Architecture decisions

* Multi-module Maven layout separates API contracts, persistence, business logic, and web concerns.
* PostgreSQL + Flyway manage schema evolution in production-like environments.
* H2 is used only for fast MockMvc tests through the `test` profile.
* `open-in-view: false` is enabled to avoid lazy-loading from the web layer.
* `@EntityGraph` is used for order queries that need related order items.
* Testcontainers validates secured API behavior against real PostgreSQL in CI.

## Tech stack

| Technology      | Version                |
| --------------- | ---------------------- |
| Java            | 17                     |
| Spring Boot     | 3.4.13                 |
| Spring Security | Managed by Spring Boot |
| Spring Data JPA | Managed by Spring Boot |
| PostgreSQL      | 16                     |
| Flyway          | Managed by Spring Boot |
| Testcontainers  | 1.20.4                 |
| Maven Wrapper   | 3.9.9                  |

## API endpoints

All `/api/**` endpoints require authentication. Use HTTP Basic Auth.

| Method | Path                    | Description                                | Success       |
| ------ | ----------------------- | ------------------------------------------ | ------------- |
| `GET`  | `/api/foods`            | List all menu items                        | `200 OK`      |
| `GET`  | `/api/orders`           | List orders for the authenticated customer | `200 OK`      |
| `GET`  | `/api/orders/{orderId}` | Get a specific order                       | `200 OK`      |
| `POST` | `/api/orders`           | Create an order from a cart payload        | `201 Created` |
| `GET`  | `/actuator/health`      | Health check                               | `200 OK`      |
| `GET`  | `/actuator/info`        | Application info                           | `200 OK`      |

## Error responses

API errors return a JSON body:

```json
{
  "error": "BAD_REQUEST",
  "message": "Cart is empty."
}
```

| Status             | `error` value   | Condition                                           |
| ------------------ | --------------- | --------------------------------------------------- |
| `400 Bad Request`  | `BAD_REQUEST`   | Empty cart, insufficient balance, or validation failure |
| `401 Unauthorized` | `UNAUTHORIZED`  | Missing or invalid credentials                      |
| `403 Forbidden`    | `FORBIDDEN`     | Order does not belong to the authenticated customer |
| `404 Not Found`    | `NOT_FOUND`     | Order not found                                     |
| `409 Conflict`     | `CONFLICT`      | Concurrent balance update (optimistic locking)        |

## Getting started

### Prerequisites

* JDK 17+
* Docker

### Run with Docker Compose

```bash
docker compose up --build
```

API available at:

```text
http://localhost:8080
```

PostgreSQL is exposed on port `5432`.

### Run locally against PostgreSQL

Start PostgreSQL and set environment variables if needed:

| Variable      | Default         |
| ------------- | --------------- |
| `DB_HOST`     | `localhost`     |
| `DB_PORT`     | `5432`          |
| `DB_NAME`     | `food_delivery` |
| `DB_USERNAME` | `food_delivery` |
| `DB_PASSWORD` | `food_delivery` |

```bash
./mvnw spring-boot:run -pl web
```

The `local` profile is active by default. Flyway applies migrations on startup.

## Build and test

```bash
./mvnw verify
```

* MockMvc tests use the `test` profile with in-memory H2.
* Testcontainers tests require Docker and start PostgreSQL automatically.
* GitHub Actions runs the build and test workflow on every push.

## Demo accounts

> **Note:** Demo passwords below are for local development and testing only.  
> In the database they are stored as BCrypt hashes; use the plain-text values with HTTP Basic Auth.

| Username | Password      | Balance |
| -------- | ------------- | ------- |
| `Smith`  | `SmithSecret` | `$100`  |
| `Jane`   | `JaneSecret`  | `$100`  |
| `John`   | `JohnSecret`  | `$10`   |

## Example requests

```bash
curl -u Smith:SmithSecret http://localhost:8080/api/foods
```

```bash
curl -u Smith:SmithSecret http://localhost:8080/api/orders
```

```bash
curl -u Smith:SmithSecret -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d @web/src/test/resources/ValidCartExample.json
```

## Project structure

```text
spring-mvc-food-delivery/
├── api/
├── persistence/
├── service/
├── web/
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-local.yml
│       ├── application-test.yml
│       └── db/migration/
│           ├── V1__create_schema.sql
│           └── V2__seed_demo_data.sql
├── Dockerfile
├── compose.yaml
└── .github/workflows/build.yml
```

## Business rules

* Orders are created from a cart.
* The order total is deducted from the customer balance.
* Empty carts are rejected.
* Orders are rejected when the customer balance is insufficient.
* Customers can only view their own orders.
* Customer balance updates use optimistic locking (`@Version`) to avoid race conditions.

## What I learned

* Structuring Spring Boot applications with Maven modules and clear boundaries.
* Managing schema evolution with Flyway.
* Testing secured REST APIs with MockMvc.
* Running PostgreSQL integration tests with Testcontainers.
* Running the application locally with Docker Compose.
* Building and testing the project automatically with GitHub Actions.

## Known limitations / next steps

* Add pagination and filtering for foods and orders.
* Add JWT authentication.
* Add admin role for menu management.
* Add API documentation page through Swagger UI.

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE).

## Author

**[Nikkilodeonee](https://github.com/Nikkilodeonee)**

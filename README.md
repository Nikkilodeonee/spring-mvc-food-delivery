# Food Delivery REST API — Spring MVC Multi-Module

A multi-module Spring Boot application for a food ordering platform. Customers browse a menu, place orders from a cart, and manage their order history — all behind Spring Security authentication.

## Overview

The project is split into four Maven modules following a layered architecture:

| Module | Responsibility |
|--------|----------------|
| `api` | OpenAPI specification and generated DTOs |
| `persistence` | JPA entities and Spring Data repositories |
| `service` | Business logic (cart, orders, balance checks) |
| `web` | REST controllers, security, exception handling |

Data is stored in an in-memory H2 database, pre-loaded with sample customers, foods, and orders.

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 17 |
| Spring Boot | 3.4.13 |
| Spring Security | (managed by Spring Boot) |
| Spring Data JPA | (managed by Spring Boot) |
| H2 Database | (in-memory) |
| OpenAPI Generator | (api module) |
| Maven | 3.x |

## API Endpoints

All `/api/**` endpoints require authentication. Use HTTP Basic Auth or the built-in form login.

| Method | Path | Description | Success |
|--------|------|-------------|---------|
| `GET` | `/api/foods` | List all menu items | `200 OK` |
| `GET` | `/api/orders` | List orders for the authenticated customer | `200 OK` |
| `GET` | `/api/orders/{orderId}` | Get a specific order | `200 OK` |
| `POST` | `/api/orders` | Create an order from a cart payload | `201 Created` |

### Error responses

| Status | Condition |
|--------|-----------|
| `400 Bad Request` | Empty cart or insufficient account balance |
| `401 Unauthorized` | Missing or invalid credentials |
| `403 Forbidden` | Order does not belong to the authenticated customer |
| `404 Not Found` | Order not found |

## Getting Started

### Prerequisites

- JDK 17+
- Apache Maven 3.8+

### Build and test

```bash
mvn test
```

### Run the application

```bash
mvn spring-boot:run -pl web
```

The server starts on `http://localhost:8080`.

### Demo accounts

| Username | Password | Balance |
|----------|----------|---------|
| `Smith` | `SmithSecret` | $100 |
| `Jane` | `JaneSecret` | $100 |
| `John` | `JohnSecret` | $10 |

### Example requests

```bash
# List menu items
curl -u Smith:SmithSecret http://localhost:8080/api/foods

# List your orders
curl -u Smith:SmithSecret http://localhost:8080/api/orders

# Place an order
curl -u Smith:SmithSecret -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d @web/src/test/resources/ValidCartExample.json
```

### H2 console

While the app is running, browse the database at `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(empty)*

## Project Structure

```
spring-mvc-food-delivery/
├── api/              # OpenAPI spec + generated models
├── persistence/      # JPA domain + repositories
├── service/          # FoodDeliveryService implementation
└── web/              # Spring Boot app, controllers, security
    └── src/main/resources/
        ├── application.properties
        └── data.sql          # Seed data
```

## Business rules

- Orders are created from a cart; the total is deducted from the customer's balance
- Orders cannot be placed with an empty cart
- Orders are rejected when the customer balance is insufficient
- Customers can only view their own orders

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE).

## Author

**[Nikkilodeonee](https://github.com/Nikkilodeonee)**

# Sales Order Management API

A RESTful Spring Boot backend for managing customers, catalog items, and sales orders — built with JWT authentication, Flyway migrations, and role-based access (USER / ADMIN).

---

## Features

* JWT-based authentication
* Role-based access control (USER / ADMIN)
* Catalog management (CRUD operations)
* Sales order creation, listing, cancellation, and deletion
* In-memory H2 database for local development
* Flyway migrations for database versioning
* Validation and exception handling with Spring Boot standards
* Postman collection included for testing

---

## Technology Stack

| Layer       | Technology              |
| ----------- | ----------------------- |
| Language    | Java 17                 |
| Framework   | Spring Boot 3.5+        |
| Build Tool  | Maven                   |
| Database    | H2 (in-memory)          |
| Migration   | Flyway                  |
| Security    | Spring Security + JWT   |
| IDE         | IntelliJ IDEA / VS Code |
| API Testing | Postman                 |

---

## Project Structure

```
sales-order-management-api/
│
├── src/main/java/com/kartikay/sales_order_management_api/
│   ├── controller/         # REST controllers
│   ├── domain/             # Entity classes
│   ├── dto/                # Data Transfer Objects
│   ├── repository/         # Spring Data JPA repositories
│   ├── service/            # Business logic
│   ├── security/           # JWT utilities and filters
│   ├── config/             # Security and global configuration
│   └── exception/          # Custom exceptions
│
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/       # Flyway SQL migration scripts
│
├── pom.xml
├── Sales Order API.postman_collection.json
└── README.md
```

---

## Prerequisites

* Java 17 or later
* Maven 3.8 or later
* Git
* Postman (for testing)

No Docker setup is required yet.

---

## How to Run Locally

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/sales-order-management-api.git
cd sales-order-management-api
```

### 2. Build the Project

```bash
mvn clean install -DskipTests
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Alternatively, in IntelliJ IDEA:

* Open the project.
* Wait for Maven dependencies to resolve.
* Run the main class:

  ```
  com.kartikay.sales_order_management_api.SalesOrderManagementApiApplication
  ```

### 4. Verify Startup

The console should show:

```
Tomcat started on port 8080
Started SalesOrderManagementApiApplication
```

---

## API Endpoints

| Endpoint                     | Method | Access      | Description                  |
| ---------------------------- | ------ | ----------- | ---------------------------- |
| `/api/v1/auth/register`      | POST   | Public      | Register a new user          |
| `/api/v1/auth/login`         | POST   | Public      | Login and get JWT token      |
| `/api/v1/catalog`            | GET    | USER, ADMIN | List all catalog items       |
| `/api/v1/catalog/{id}/price` | PUT    | ADMIN       | Update catalog item price    |
| `/api/v1/orders`             | GET    | USER, ADMIN | List all orders with filters |
| `/api/v1/orders`             | POST   | USER, ADMIN | Create a new order           |
| `/api/v1/orders/{id}`        | GET    | USER, ADMIN | Get specific order details   |
| `/api/v1/orders/{id}/cancel` | PUT    | ADMIN       | Cancel an existing order     |
| `/api/v1/orders/{id}`        | DELETE | ADMIN       | Delete an order              |

---

## H2 Database Console

Accessible at:

```
http://localhost:8080/h2-console
```

Use the following credentials:

| Property | Value                 |
| -------- | --------------------- |
| JDBC URL | `jdbc:h2:mem:salesdb` |
| Username | `sa`                  |
| Password | *(leave blank)*       |

---

## Using the Postman Collection

### 1. Import Collection

* Open Postman
* Click **Import → Upload Files**
* Select `Sales Order API.postman_collection.json`

### 2. Create an Environment

Create an environment with:

| Variable   | Value                        |
| ---------- | ---------------------------- |
| `base_url` | `http://localhost:8080`      |
| `token`    | (will be filled after login) |

### 3. Run the Workflow

1. Run `Register User` to create a new account.
2. Run `Login` to receive a JWT token.
3. Copy the token to your environment variable `token`.
4. Use the other endpoints with the Authorization header automatically included:

   ```
   Authorization: Bearer {{token}}
   ```

---

## Authentication Flow

1. **Register** a user
   Endpoint: `/api/v1/auth/register`
   Example:

   ```json
   {
     "username": "john",
     "password": "12345"
   }
   ```

2. **Login**
   Endpoint: `/api/v1/auth/login`
   Response:

   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9..."
   }
   ```

3. Use the token for all authenticated requests:

   ```
   Authorization: Bearer <JWT_TOKEN>
   ```

---

## Common Issues and Fixes

| Issue                                      | Solution                                                                   |
| ------------------------------------------ | -------------------------------------------------------------------------- |
| `Wrong username or password [28000]`       | Ensure H2 credentials: username `sa`, blank password                       |
| `failed to lazily initialize a collection` | Ensure service method uses `@Transactional(readOnly = true)` or fetch join |
| `403 Forbidden`                            | Add a valid JWT token in Postman                                           |
| `Flyway migration failed`                  | Delete `.h2` cache and restart the app                                     |

---

## Future Enhancements

* Docker and PostgreSQL support
* CI/CD integration (GitHub Actions + Azure)


---

## Author

**Kartikay Kumar**

Backend Developer (Java & Spring Boot)
Project: Sales Order Management API



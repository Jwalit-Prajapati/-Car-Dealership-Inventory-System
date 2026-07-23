# Car Dealership Inventory System

## Overview
A comprehensive RESTful API built with Spring Boot for managing a car dealership's inventory. The system handles user management, role-based authentication, vehicle inventory tracking, and purchase processing. It is designed with a robust layered architecture, utilizing modern Java practices and containerized dependencies.

## Key Features
- **User Authentication & Authorization**: Secure API endpoints using Spring Security and JSON Web Tokens (JWT).
- **User Management**: Manage users and their roles, storing comprehensive profiles including addresses and driving license details.
- **Vehicle Inventory**: Track vehicle stock by make, model, category, and price. Includes atomic operations to add and reduce stock, preventing negative inventory.
- **Purchase Processing**: Record transactions linking users to vehicle purchases, tracking historical pricing and quantities.
- **Dashboard Analytics**: Exposes high-level metrics and statistics via dedicated dashboard endpoints.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Persistence**: Spring Data JPA & Hibernate
- **Database Migrations**: Flyway
- **Object Mapping**: MapStruct
- **Boilerplate Reduction**: Lombok
- **Security**: Spring Security & `jjwt`
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito, & Testcontainers (for reliable database integration tests)

## Project Structure
The application follows a standard layered architecture:
- `controller/`: REST API endpoints handling HTTP requests.
- `service/`: Core business logic layer.
- `repository/`: Spring Data JPA interfaces for database interaction.
- `entity/`: JPA domain models (`User`, `Vehicle`, `Purchase`, etc.).
- `dto/`: Data Transfer Objects for client-server communication.
- `mapper/`: MapStruct interfaces for converting between Entities and DTOs.
- `security/`: JWT filters, authentication providers, and security configurations.
- `validation/`: Custom validation constraints and logic.
- `exception/`: Global exception handling (e.g., `InsufficientStockException`).

## Getting Started

### Prerequisites
- JDK 17
- Node.js 20+ (only for running the frontend outside Docker)
- Docker and Docker Compose

### Local Development Setup

1. **Start the Database**
   The project includes a root-level `docker-compose.yml` to quickly spin up a PostgreSQL instance.
   ```bash
   docker-compose up -d db
   ```

2. **Set required environment variables**
   The app reads its JWT signing key from `JWT_SECRET` (`backend/src/main/resources/application.yml`) — there is no default, so the app won't boot without it. It must be **base64-encoded** (it's decoded as a key, not used as raw text). Generate one and export it before running natively:
   ```bash
   openssl rand -base64 32
   export JWT_SECRET=<paste generated value>   # PowerShell: $env:JWT_SECRET = "<value>"
   ```
   (This is only needed when running the backend directly via `mvnw`/step 3 below — `docker-compose up -d` in step 4 already sets `JWT_SECRET` for the containerized app.)

3. **Run the Backend**
   Use the included Maven wrapper to start the Spring Boot application.
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   The application will automatically connect to the local PostgreSQL database, apply Flyway migrations, and start on port `8080`.

   To run the frontend against it, in a separate terminal:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   The SPA starts on port `5173` and talks to the backend at `http://localhost:8080` (`frontend/.env` → `VITE_API_BASE_URL`).

4. **Run via Docker Compose (Full Stack)**
   Alternatively, run the database, backend, and frontend together as containers from the project root:
   ```bash
   docker-compose up -d --build
   ```
   - Backend: `http://localhost:8080`
   - Frontend: `http://localhost:3000` — served by nginx, which also reverse-proxies `/api/*` to the backend container, so the browser never makes cross-origin calls.

## Testing
This project embraces Test-Driven Development (TDD) with isolated, fast-running tests. Persistence layer tests are backed by Testcontainers for true database verification without mocking the database engine.

Run the test suite using:
```bash
cd backend
./mvnw test
```

## API Documentation

### Authentication (`/api/auth`)
* `POST /api/auth/login`: Authenticate a user and return a JWT token.
* `POST /api/auth/register`: Register a new user account.

### Vehicles (`/api/vehicles`)
* `GET /api/vehicles`: Get a paginated list of all vehicles.
* `GET /api/vehicles/search`: Search vehicles using filters (e.g., make, category, min/max price) with pagination.
* `POST /api/vehicles`: Create a new vehicle in the inventory. (Admin only)
* `PUT /api/vehicles/{id}`: Update an existing vehicle's details. (Admin only)
* `DELETE /api/vehicles/{id}`: Delete a vehicle from the inventory. (Admin only)
* `POST /api/vehicles/{id}/restock`: Increase the stock quantity of a specific vehicle. (Admin only)

### Purchases (`/api/purchases`)
* `POST /api/purchases`: Process a vehicle purchase for the authenticated user, automatically reducing inventory stock.

### Dashboard Statistics (`/api/admin/stats`)
* `GET /api/admin/stats/inventory`: Retrieve aggregated inventory statistics (e.g., total vehicles, low stock items). (Admin only)
* `GET /api/admin/stats/sales`: Retrieve aggregated sales metrics (e.g., total revenue, most purchased vehicles). (Admin only)

### System (`/api`)
* `GET /api/health`: Basic health check endpoint returning system status.

## My AI Usage
This README.md file was generated by an AI assistant by comprehensively analyzing the project's `pom.xml`, entity classes (`User`, `Vehicle`, `Purchase`), controllers, and existing configuration files to document the architecture, features, and setup instructions.
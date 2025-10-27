# 🔐 MyAuth API — Authentication Service

> 🚧 **Work in Progress** — this API is currently under active development.

**MyAuth API** is an authentication and secret management service built with **Spring Boot**.  
It aims to provide a robust API for **user registration**, **JWT-based login**, and **secure secret management** (2FA tokens).

---

## 🧩 Core Technologies

- ☕ **Java 21+**
- 🌱 **Spring Boot**
- 🔒 **Spring Security** — Authentication & Authorization via JWT  
- 🗃️ **Spring Data JPA** — Database interaction  
- 🐘 **PostgreSQL** — Relational database  
- ⚙️ **Maven** — Dependency management  
- 🧱 **Flyway** — Database schema migrations  
- 🐳 **Docker & Docker Compose** — Development & deployment environment  
- 🧪 **Testcontainers** — Integration testing with a real PostgreSQL instance  
- ✅ **JUnit 5** — Unit & integration testing  

---

## 🏗️ Project Architecture

MyAuth follows a **Vertical Slice Architecture** approach — organizing code by feature rather than by layer.  
This separation makes the project easier to maintain and scale.

- `src/main/java/com/myauth`
    - `common/`: Shared utility classes, used for **Result Pattern** implementation

    - `features/`: Contains the business logic for each feature (e.g., userlogin, addsecret).

        - `...Controller.java`: Exposes the API (endpoints).

        - `...Handler.java`: Contains the main business logic for the feature.

    - `infrastructure/`: Contains the technical "plumbing".

        - `db/`: JPA Entities (User, Secret, Device) and Repositories.

        - `security/`: Spring Security configuration, SecurityFilter (for JWT), TokenService, and CustomAuthenticationEntryPoint (for 401 errors).

- `src/main/resources`
    - `db/migration/`: Flyway migration scripts (e.g., V1__create-tables.sql).


---

## ⚙️ How to Run

### 🧾 Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose

---

### 🐳 Run with Docker Compose (Recommended)

This is the easiest way to spin up the full stack (API + Database).

#### 1️⃣ Create a `.env` file in the project root:

```env
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
JWT_SECRET=
```
#### 2️⃣ Build and start the containers:
```bash
docker-compose up --build -d
```
API available at:

👉 http://localhost:8080


## 🧪 Running Tests

The project uses Testcontainers, meaning integration tests automatically start a temporary PostgreSQL container.

```bash
mvn verify
```

## 📚 API Endpoints (Current Status)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
|POST    |/api/auth/register |Registers a new user |❌ No |
|POST    |/api/auth/login    | Authenticates user & returns JWT |❌ No |
|POST    |/api/auth/secret   | Adds a new user secret | ✅ Yes |

## 🚧 Work in Progress — To-Do List
### 🔐 Feature: Secret Management

- [ ] GET /api/auth/secret — List all user secrets

- [ ] DELETE /api/auth/secret/{id} — Remove a secret

### 💻 Feature: Device Management

- [ ] GET /api/auth/device — List devices associated with user

- [ ] DELETE /api/auth/device/{id} — Disconnect a device (remote logout)

🕒 Feature: TOTP (2FA Code Generation)
- [ ] Generation of valid TOTP codes
- [ ] Send the codes to the users
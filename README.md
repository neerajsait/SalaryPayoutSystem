# Salary Payout System

An enterprise-grade, event-driven microservices template built with **Spring Boot 3.2.11**, **Java 21**, **Kafka**, and **Redis**. This project provides a fully functioning, decoupled architecture for managing employee data, batch-generating payroll, integrating with Stripe for payments, and dispatching PDF payslips via email.

It is designed as a **ready-to-deploy template**. By simply cloning the repository and filling in an `.env` file, any business can instantly deploy their own robust payroll system.

---

## 🏛️ Architecture Overview

The system strictly adheres to Database-per-Service and Event-Driven architecture patterns, ensuring zero synchronous HTTP coupling between backend services.

### Microservices
1. **FrontendService** (Port `8080`)
   - The user interface built with JSP, HTML, and Vanilla CSS.
   - Secured via Spring Security (intercepts unauthenticated users and forces login).
   - Serves as the UI gateway to the backend APIs.
2. **EmployeeService** (Port `8081`)
   - **Database**: `salarypayoutsystem_employee`
   - Manages Employee data and Salary Records (including batch generation).
   - **Caching**: Uses Redis to cache frequent reads and active employee lists.
   - **Rate Limiting**: Uses Bucket4j to limit expensive batch generation endpoints.
3. **PaymentService** (Port `8083`)
   - **Database**: `salarypayoutsystem_payment`
   - Handles Stripe integration and webhooks.
   - Strictly consumes and publishes Kafka events.
4. **AuditService** (Port `8084`)
   - A lightweight observer service that consumes lifecycle events (`employee.updated`, `salary.generated`) to maintain an append-only audit log.

### Event-Driven Flow (Kafka)
Synchronous `RestTemplate` calls have been completely replaced with asynchronous Kafka topics:
- `salary.generated` → `PaymentService` processes the Stripe transfer.
- `payment.succeeded` / `payment.failed` → `EmployeeService` updates the database status.
- `notification.send` → `EmployeeService` generates and emails a PDF payslip, decoupling email delivery from critical payment processing.

---

## 🚀 Quick Start & Deployment

### 1. Prerequisites
- Java 21+
- Maven
- Docker Desktop (for Kafka, Redis, Zookeeper, and Zipkin)
- MySQL Server (running locally on port 3306)

### 2. Infrastructure (Docker Compose)
Before starting the Java services, spin up the backing infrastructure:
```bash
docker compose up -d
```
This launches:
- **Redis** (Port `6379`)
- **Kafka** (Port `9092`) & **Zookeeper** (Port `2181`)
- **Zipkin** (Port `9411` — Distributed Tracing Dashboard)

### 3. Environment Variables
Create an `.env` file in the root of the project (next to `docker-compose.yml`). The Spring Boot services are configured to automatically load this file.

```properties
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# Admin Credentials for Frontend Login
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123

# Stripe Payment Gateway
STRIPE_API_KEY=sk_test_...

# Email Configuration (for PDF Payslips)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Infrastructure (Defaults align with docker-compose)
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 4. Running the Services
The databases (`salarypayoutsystem_employee` and `salarypayoutsystem_payment`) will be automatically created on startup. Start the services in your IDE or via Maven:
1. `EmployeeService`
2. `PaymentService`
3. `AuditService`
4. `FrontendService`

Access the dashboard at: **http://localhost:8080**

---

## 🛡️ Enterprise Features Included

*   **Idempotency Guarantees**: Database-level unique constraints on `(employee_id, month, year)` prevent accidental double-payouts, even under concurrent race conditions.
*   **Distributed Tracing**: With Micrometer and Brave, every request is traced across the stack. Open **http://localhost:9411** (Zipkin) to view a visual timeline of a single request spanning the Frontend, EmployeeService, Kafka, and PaymentService.
*   **Rate Limiting**: Protects expensive endpoints (like batch salary processing) from abuse using Bucket4j.
*   **CORS Hardening**: Backend services reject cross-origin requests that do not originate from the FrontendService.
*   **Global Exception Handling**: Prevents raw Java stack traces from leaking to API clients via `@RestControllerAdvice`.

# Simple CRM 📊
A lightweight Customer Relationship Management system for managing sellers and their transactions.

---

## Features
- **Seller & Transaction Management:** Create, retrieve, update, soft-delete, and hard-delete sellers and transactions.
- **Analytics:**
    - Identify the top seller within a given period.
    - Find sellers with a turnover below a specified amount.
- **Data History Preservation:**
    - Versioning of database records for `Update` operations.
    - Delete Types:
        - **Soft-delete:** Records are marked as deleted but remain in the database.
        - **Hard-delete:** Records are permanently removed from the database.

---

## Technology Stack

### Backend
- **Java 21** (Gradle)
- **Spring Boot 3.4+** (Data JPA, WebMVC, Test)
- **PostgreSQL 12+** — primary database
- **Flyway** — database migration management
- **H2** — in-memory database for integration testing
- **SwaggerUI (OpenAPI)** — automatic API documentation
- **Spring Boot Test (JUnit 5 + Mockito)** — unit and integration testing
- **Lombok**

### Tools
- **Gradle** — build and dependency management
- **Postman** — manual API testing and debugging

---

## Build and Run Instructions

### Prerequisites
- Java 21
- Gradle 8.0+
- PostgreSQL 12+

### Database Setup
1. Create a PostgreSQL database, e.g. `simple_crm_db`.
2. Ensure the user has read/write permissions.

### Application Configuration
1. Create a `.env` file under the `Presentation/.env` directory.
2. Configure your database connection according to the example in `.env.example`.
3. Create a `gradle.properties` file in the project root.
4. Configure Flyway settings based on `gradle.properties.example`.
5. Verify the configured data source in your IDE.

### Build the Project and Run Database Migrations

```bash

# Stop all running Spring Boot processes (if any)
taskkill /f /im java.exe

# Clean and build the project
./gradlew clean build --refresh-dependencies

# Run Flyway migrations
./gradlew :Presentation:flywayClean :Presentation:flywayMigrate

# Start the application
./gradlew :Presentation:bootRun
```

> [!WARNING]
> Flyway rollbacks are available only in the Pro or Enterprise editions. If you encounter migration issues (missing tables, invalid flyway_schema_history, etc.), manually recreate the necessary tables using PgAdmin or your IDE’s SQL console.

If the build succeeds, the application will be available at: http://localhost:8080

---

## API Examples

**Request:**
```http request
POST http://localhost:8080/api/sellers
Content-Type: application/json

{
    "name": "John Doe",
    "contactInfo": "john.doe@example.com",
    "registrationDate": "2025-10-07T12:30:00"
}
```
**Response:**
```
201 Created
{
    "seller": {
        "id": 1,
        "name": "John Doe",
        "contactInfo": "john.doe@example.com",
        "registrationDate": "2025-10-10T19:46:19.7552163",
        "version": null
    },
    "message": null,
    "errorType": null
}
```
---
**Request:**
```http request
DELETE http://localhost:8080/api/transactions/1?deleteType=soft
```
**Response:**
```
204 No Content
```
---
**Request:**
```http request
GET http://localhost:8080/api/analytics/best-period/1
Content-Type: application/json
{
  "amount": 65.05,
  "paymentType": "CASH",
  "transactionDate" : "2025-09-07T14:00:00",
  "sellerId": 1
}
```
**Response:**
```
200 OK
{
    "startDate": "2025-06-07T14:00:00",
    "endDate": "2025-10-07T14:00:00",
    "transactionCount": 3
}
```

Once the application is running, the full API specification is available at:
http://localhost:8080/swagger-ui/index.html

---

## Testing
- Unit Tests for entities, DTOs, services, and utilities.
  Average coverage: 90%
- API Tests covering all main usage scenarios.
- Integration Tests for repositories using an H2 in-memory database.

### Run All Tests:
```bash
# run tests
./gradlew test
```

# Generate coverage report with JaCoCo
```bash
# generate JaCoCo coverage reports
./gradlew jacocoRootReport
```
Code coverage reports are available at: `build/reports/jacoco/rootHtml/index.html`

---

## Docker Deployment

The application can be easily deployed using Docker with the provided [Dockerfile](file://D:\Projects\IntellijProjects\shift-lab-simple-crm\Dockerfile) and [docker-compose.yaml](file://D:\Projects\IntellijProjects\shift-lab-simple-crm\docker-compose.yaml) configuration files.

### Prerequisites for Docker Deployment
- Docker Engine 20.10+
- Docker Compose v2.0+

### Quick Start with Docker Compose

1. **Create a `.env` file** in the project root directory with your database credentials:
   ```env
   DB_USER=postgres
   DB_PASSWORD=your_secure_password
   DB_URL=jdbc:postgresql://db:5432/SimpleCrmDb
   ```


2. **Build and start the services**:
   ```bash
   # Build the Docker images
   docker-compose build
   
   # Start all services
   docker-compose up -d
   ```


3. **Access the application**:
   Once the containers are running, the application will be available at: http://localhost:8080

### Docker Configuration Files

- **`Dockerfile`**: Multi-stage build configuration for the Spring Boot application with security best practices
- **`docker-compose.yaml`**: Production-ready configuration with environment variables and resource limits
- **`docker-compose.override.yaml`**: Development configuration with volume mounts for live code reloading

### Environment Variables

The Docker deployment uses the following environment variables (can be set in `.env` file):

- `DB_NAME`: Database name (default: SimpleCrmDb)
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `DB_PORT`: Database port (default: 5432)
- `APP_PORT`: Application port (default: 8080)
- `SPRING_PROFILES_ACTIVE`: Spring profile to use (default: docker)

### Useful Docker Commands

```bash
# View container logs
docker-compose logs -f

# Stop all services
docker-compose down

# View running containers
docker-compose ps

# Execute commands in the running container
docker-compose exec app bash
```


### Security Features

- Runs application with non-root user
- Resource limits to prevent DoS attacks
- Health checks for service monitoring
- Environment variables for secret management
- Minimal base images to reduce attack surface

---

## License
This project is licensed under the MIT License - see the [LICENSE.md](docs/src/LICENSE.md) file for details.

---

## Contact
For questions or feedback: limosha@inbox.ru

---
Feel free to customize this further to better fit your needs!
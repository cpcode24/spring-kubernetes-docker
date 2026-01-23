# Spring Kubernetes Docker — demo

This repository contains a small Spring Boot demo application ("demo") and deployment artifacts for running it locally or inside a local Kubernetes cluster (kind). The service models a simple catalog for services, products, vendors and customers and exposes REST endpoints for CRUD operations.

## What the application does

- Provides REST APIs for customers, products, services and vendors.
- Persists data in a PostgreSQL database (schema `demo`).
- Exposes observability via Spring Boot Actuator and Micrometer (Prometheus registry available at runtime).
- Includes sample SQL to populate the DB with demo data (see `dat/data.sql`).

Core REST endpoints (base path) provided by the application
- GET/POST/PUT/DELETE /customers
- GET/POST/PUT/DELETE /products
- GET/POST/PUT/DELETE /services
- GET/POST/PUT/DELETE /vendors

(Controllers are in `demo/src/main/java/com/kubertest/java/demo/web/`.)

## Tech stack

- Java 21
- Spring Boot 3.x (starter web, data-jpa, actuator)
- Spring Data JPA + Hibernate
- PostgreSQL (runtime)
- Micrometer (Prometheus registry)
- Lombok
- Logstash Logback encoder
- Docker, kind (for local Kubernetes)
- Maven (project build) — `./mvnw` wrapper included in `demo/`

## Quick start — run locally ( JVM )

1. Start PostgreSQL (example using Docker):

   export DB_USERNAME=localuser
   export DB_PASSWORD='p@ssw0Rd!'
   export DB_NAME=localdb
   export DB_PORT=5432
   export DB_HOST=127.0.0.1

   docker run --name localdb -e POSTGRES_DB=${DB_NAME} -e POSTGRES_USER=${DB_USERNAME} -e POSTGRES_PASSWORD=${DB_PASSWORD} -p ${DB_PORT}:5432 -d postgres:15

2. Create schema and load demo data:

   PGPASSWORD=${DB_PASSWORD} psql -h ${DB_HOST} -U ${DB_USERNAME} -d ${DB_NAME} -f dat/data.sql

3. Build and run the Spring Boot app:

   cd demo
   ./mvnw clean package -DskipTests
   DB_HOST=${DB_HOST} DB_PORT=${DB_PORT} DB_NAME=${DB_NAME} DB_USERNAME=${DB_USERNAME} DB_PASSWORD=${DB_PASSWORD} java -jar target/demo-0.0.1-SNAPSHOT.jar

The app will start on port 8080 by default. Actuator endpoints (health/info) are available under `/actuator`.

## Run in a local Kubernetes cluster (kind)

This repository includes scripts and Kubernetes manifests to deploy a local cluster and the demo app.

- Bring up infrastructure and postgres, then populate DB:

  ./scripts/install.sh

This script will deploy a kind cluster, ingress, monitoring stack, a PostgreSQL instance (see `config/postgres`) and run `scripts/populate-db.sh` to load `dat/data.sql` into the DB.

- After deployment you can forward the service port locally or access via the ingress configured by the demo manifests (see `demo/deployment`). Example port-forward:

  kubectl -n demo port-forward svc/demo 8080:8080

Adjust namespace/service names according to the YAML in `demo/deployment/`.

## Test the API (examples)

- List all customers

  curl -s http://localhost:8080/customers | jq

- Get a customer by id

  curl -s http://localhost:8080/customers/<UUID>

- Create a customer

  curl -X POST http://localhost:8080/customers \
    -H "Content-Type: application/json" \
    -d '{"customerId":"<uuid>","firstName":"Jane","lastName":"Doe","email":"jane@example.com"}'

- Health

  curl http://localhost:8080/actuator/health

- Metrics (Prometheus)

  (metrics are available via the Micrometer Prometheus registry when configured by the deployment/monitoring stack)

## Database

- Schema and sample data are in `dat/data.sql`.
- The application reads DB connection settings from environment variables (see `demo/src/main/resources/application.properties`).
- There is a sample env file at `demo/env.list` showing the expected variables.

## Build and Docker

- The demo module contains a Dockerfile (`demo/Dockerfile`) you can use to build a container image for the app.

  cd demo
  docker build -t demo-api:latest .

Push and deploy this image in your registry/cluster as required by your environment.

## Notes and troubleshooting

- Use the included Maven wrapper (`demo/mvnw`) to avoid local Maven version issues.
- If you changed DB credentials, update `demo/env.list` or supply matching environment variables when starting the app.
- The `scripts/install.sh` script assumes `kind`, `kubectl`, `docker` and related tools are installed and available on PATH.


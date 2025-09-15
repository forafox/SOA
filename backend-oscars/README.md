# backend-oscars

Spring Boot (Java 21, Gradle Kotlin DSL) service implementing the second web-service per task.

## Requirements
- Java 21 (Temurin recommended)
- Gradle Wrapper (included)
- Docker (optional for container build)

## Build
```bash
./gradlew clean build
```

## Run locally
```bash
./gradlew bootRun
# or
java -jar build/libs/backend-oscars-0.0.1-SNAPSHOT.jar
```
Service listens on `http://localhost:8080`.

## Docker
```bash
# Build image
docker build -t backend-oscars:local .

# Run container
docker run --rm -p 8080:8080 backend-oscars:local
```

## CI
GitHub Actions workflow builds and tests on pushes/PRs affecting `backend-oscars/`.

## Notes
- This module is self-contained. No changes are made in `frontend` or `backend-films`.
- API implementation will follow `../swagger.yaml` in subsequent steps.

# SOA Monorepo

This repository hosts multiple services. This commit adds the second backend service: `backend-oscars` (Spring Boot, Java 21, Gradle Kotlin DSL).

## Structure
- `backend-oscars/` – Spring Boot service (this change)
- `frontend/` – untouched
- `backend-films/` – untouched

## CI
GitHub Actions workflow at `.github/workflows/backend-oscars-ci.yml` builds and tests the `backend-oscars` module on changes.

## Docker Compose
At repo root:
```bash
docker compose up --build backend-oscars
```
Service will be available at `http://localhost:8080`.

## Local development for backend-oscars
```bash
cd backend-oscars
./gradlew clean build
./gradlew bootRun
```

## Notes
- Company package/group: `com.jellyone`.
- The other modules remain unchanged by design.
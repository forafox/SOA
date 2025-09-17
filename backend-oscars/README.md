# Backend Oscars Service

Веб-сервис для управления наградами Оскар, реализованный на Spring Boot с использованием Java 21.

## Описание

Этот сервис является вторым веб-сервисом в архитектуре SOA, который работает с данными о фильмах и предоставляет дополнительные операции с наградами Оскар. Сервис взаимодействует с первым сервисом (Movies) для получения данных о фильмах и выполнения операций с наградами.

## Технологии

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Web MVC**
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Gradle Kotlin DSL**
- **Lombok**

## Функциональность

### API Endpoints

#### Oscars Operations

- `GET /oscars/operators/losers` - Получить список персон (сценаристов) из фильмов без Оскаров
- `POST /oscars/movies/honor-by-length/{minLength}` - Наградить фильмы с длиной больше указанной
- `POST /oscars/movies/honor-low-oscars` - Наградить фильмы с минимальным количеством Оскаров
- `GET /oscars/movies/{movieId}` - Получить все Оскары по конкретному фильму
- `POST /oscars/movies/{movieId}` - Добавить Оскары к фильму
- `DELETE /oscars/movies/{movieId}` - Удалить все Оскары у фильма

### Swagger UI

Интерактивная документация API доступна по адресу:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Запуск

### Локальная разработка

```bash
# Перейти в директорию проекта
cd backend-oscars

# Собрать проект
./gradlew clean build

# Запустить приложение
./gradlew bootRun
```

### Docker

```bash
# Из корневой директории проекта
docker compose up --build backend-oscars
```

## Конфигурация

Основные настройки находятся в `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: backend-oscars
server:
  port: 8080
movies:
  api:
    base-url: http://localhost:8081  # URL первого сервиса Movies
```

## Архитектура

### Компоненты

- **OscarsController** - REST контроллер для обработки HTTP запросов
- **OscarsService** - Бизнес-логика для работы с наградами
- **MoviesClient** - HTTP клиент для взаимодействия с сервисом Movies
- **WebClientConfig** - Конфигурация HTTP клиента

### Модели данных

- **Person** - Персона (сценарист, оператор и т.д.)
- **Movie** - Фильм с информацией о наградах
- **MoviePatch** - Обновление данных фильма
- **Coordinates** - Координаты фильма
- **MovieGenre** - Жанр фильма

## Требования

- Java 21+
- Gradle 8+
- Доступ к сервису Movies (порт 8081)

## Разработка

### Структура проекта

```
src/
├── main/
│   ├── java/com/jellyone/oscars/
│   │   ├── client/          # HTTP клиенты
│   │   ├── config/          # Конфигурации
│   │   ├── controller/      # REST контроллеры
│   │   ├── exception/       # Обработка исключений
│   │   ├── model/           # Модели данных
│   │   └── service/         # Бизнес-логика
│   └── resources/
│       └── application.yaml # Конфигурация приложения
└── test/                    # Тесты
```

### Тестирование

```bash
# Запуск всех тестов
./gradlew test

# Запуск с отчетом
./gradlew test jacocoTestReport
```

## Интеграция

Сервис интегрируется с:
- **Movies Service** - для получения данных о фильмах
- **Frontend** - для отображения пользовательского интерфейса

## Безопасность

- Все запросы должны выполняться по HTTPS (в продакшене)
- Валидация входных данных
- Обработка ошибок и исключений

## Мониторинг

- Spring Boot Actuator для health checks
- Логирование через SLF4J + Logback
- Метрики через Micrometer

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

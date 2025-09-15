# SOA Monorepo - Movies & Oscars System

Монорепозиторий для системы управления фильмами и наградами Оскар, реализованной в рамках лабораторной работы по архитектуре сервис-ориентированных приложений (SOA).

## Описание проекта

Система состоит из двух веб-сервисов и клиентского приложения:

1. **Backend Films** - Первый ("вызываемый") веб-сервис на JAX-RS + WildFly
2. **Backend Oscars** - Второй веб-сервис на Spring MVC REST + WildFly  
3. **Frontend** - Клиентское приложение для взаимодействия с сервисами

## Архитектура

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │ Backend Films   │    │ Backend Oscars  │
│   (Client)      │◄──►│   (JAX-RS)      │◄──►│  (Spring MVC)   │
│                 │    │   (WildFly)     │    │   (WildFly)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Структура проекта

```
soa/
├── backend-films/          # Первый веб-сервис (JAX-RS + WildFly)
├── backend-oscars/         # Второй веб-сервис (Spring MVC + WildFly)
├── frontend/               # Клиентское приложение
├── swagger.yaml           # OpenAPI спецификация
├── docker-compose.yaml    # Docker Compose конфигурация
└── README.md              # Этот файл
```

## Технологический стек

### Backend Films
- **Java 21**
- **JAX-RS** (RESTful API)
- **WildFly** (Application Server)
- **Maven** (Build Tool)

### Backend Oscars  
- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Web MVC**
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Gradle Kotlin DSL**
- **WildFly** (Application Server)

### Frontend
- Веб-фреймворк для сервера helios
- Полный набор возможностей API обоих сервисов
- Сортировка, фильтрация, постраничный вывод
- Человеко-читаемое представление данных
- Обработка ошибок сервисов

## API Документация

### Swagger UI
- **Backend Oscars**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### OpenAPI Спецификация
Полная спецификация API доступна в файле `swagger.yaml` в корне проекта.

## Запуск системы

### Docker Compose (Рекомендуется)

```bash
# Запуск всех сервисов
docker compose up --build

# Запуск только backend-oscars
docker compose up --build backend-oscars
```

### Локальная разработка

#### Backend Films
```bash
cd backend-films
# Следуйте инструкциям в README.md проекта
```

#### Backend Oscars
```bash
cd backend-oscars
./gradlew clean build
./gradlew bootRun
```

#### Frontend
```bash
cd frontend
# Следуйте инструкциям в README.md проекта
```

## Порты сервисов

- **Backend Films**: 8081 (HTTP), 8443 (HTTPS)
- **Backend Oscars**: 8080 (HTTP), 8443 (HTTPS)  
- **Frontend**: 3000 (или другой порт)

## Безопасность

- Все сервисы поддерживают HTTPS с самоподписанными сертификатами
- HTTP доступ запрещен в продакшене
- Валидация входных данных на всех уровнях

## Требования к развертыванию

### Сервер helios
- Все компоненты системы должны быть развернуты на сервере helios
- Поддержка HTTPS с самоподписанными сертификатами
- Доступность через веб-интерфейс

### Функциональные требования

#### Backend Films
- Полная реализация API согласно спецификации
- Поддержка CRUD операций с фильмами
- Фильтрация, сортировка, пагинация
- Валидация данных

#### Backend Oscars  
- Интеграция с Backend Films
- Операции с наградами Оскар
- Статистика и аналитика
- Swagger UI документация

#### Frontend
- Полный набор возможностей API
- Сортировка, фильтрация, постраничный вывод
- Человеко-читаемое представление данных
- Обработка ошибок и валидации

## Разработка

### CI/CD
- GitHub Actions для автоматической сборки и тестирования
- Workflow файлы в `.github/workflows/`

### Пакетная структура
- **Backend Films**: `com.jellyone.films`
- **Backend Oscars**: `com.jellyone.oscars`

### Тестирование
```bash
# Backend Oscars
cd backend-oscars
./gradlew test

# Backend Films  
cd backend-films
# Следуйте инструкциям проекта
```

## Мониторинг и логирование

- Spring Boot Actuator (Backend Oscars)
- Health checks для всех сервисов
- Централизованное логирование
- Метрики производительности

## Документация

- [Backend Films README](backend-films/README.md)
- [Backend Oscars README](backend-oscars/README.md)
- [Frontend README](frontend/README.md)
- [OpenAPI Specification](swagger.yaml)

## Контакты

- **Компания**: JellyOne
- **Пакетная группа**: `com.jellyone`
- **Версия API**: 1.0.3

## Лицензия

Проект создан в рамках учебного задания по архитектуре SOA.
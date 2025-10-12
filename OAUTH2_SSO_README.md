# Keycloak OAuth2/OIDC SSO Integration for SOA Application

## 🎯 Обзор проекта

Успешно интегрирована система единого входа (SSO) на основе Keycloak OAuth2/OIDC для SOA-приложения, состоящего из:
- **Frontend**: Next.js приложение
- **Backend Films**: JAX-RS/WildFly сервис
- **Backend Oscars**: Spring Boot сервис
- **Keycloak**: Identity Provider для аутентификации
- **PostgreSQL**: База данных для всех сервисов

## 🏗️ Архитектура решения

```
┌─────────────────┐    OAuth2/OIDC    ┌─────────────────┐
│   Frontend      │ ──────────────────► │   Keycloak      │
│   (Next.js)     │ ◄────────────────── │   (IdP)         │
└─────────────────┘                     └─────────────────┘
         │                                       │
         │ JWT Token                             │
         ▼                                       ▼
┌─────────────────┐                     ┌─────────────────┐
│ Backend Films   │ ◄─── JWT Token ──── │ Backend Oscars  │
│ (JAX-RS/WildFly)│                     │ (Spring Boot)   │
└─────────────────┘                     └─────────────────┘
         │                                       │
         └─────────────── PostgreSQL ────────────┘
```

## 🔧 Реализованные компоненты

### 1. Keycloak Configuration
- **Realm**: `soa-realm`
- **OAuth2 Clients**: 
  - `backend-films` - для backend-films сервиса
  - `backend-oscars` - для backend-oscars сервиса  
  - `frontend` - для фронтенда
- **Test User**: `testuser` / `test123`

### 2. Backend Films (JAX-RS/WildFly)
- **OAuth2 Resource Server** конфигурация
- **JWT Token Validation** для входящих запросов
- **JWT Filter** для межсервисного взаимодействия
- **Security Context** интеграция

### 3. Backend Oscars (Spring Boot)
- **Spring Security OAuth2** конфигурация
- **JWT Service** для генерации токенов
- **MoviesApiClient** для вызовов backend-films
- **OAuth2 Resource Server** поддержка

### 4. Frontend (Next.js)
- **OAuth2AuthService** для управления аутентификацией
- **AuthComponent** для UI взаимодействия
- **API Client** с автоматическим добавлением токенов
- **State Management** для пользовательских данных

## 🚀 Запуск и тестирование

### 1. Запуск всех сервисов
```bash
docker-compose up --build -d
```

### 2. Настройка Keycloak
```bash
./keycloak-setup.sh
```

### 3. Доступ к сервисам
- **Frontend**: http://localhost:3000
- **Backend Films**: http://localhost:8080/api/movies
- **Backend Oscars**: http://localhost:8081/oscars/movies
- **Keycloak Admin**: http://localhost:8082/admin

### 4. Тестирование аутентификации
1. Откройте http://localhost:3000
2. Нажмите "Login with Keycloak OAuth2"
3. Войдите как `testuser` / `test123`
4. Проверьте доступ к защищенным API

## 🔐 Безопасность

### OAuth2/OIDC Flow
1. **Authorization Code Flow** для фронтенда
2. **Client Credentials** для межсервисного взаимодействия
3. **JWT Tokens** для stateless аутентификации
4. **PKCE** для дополнительной безопасности

### Межсервисная аутентификация
- **JWT Tokens** генерируются backend-oscars
- **Shared Secret** для подписи токенов
- **Token Validation** в backend-films
- **Role-based Access Control**

## 📁 Структура файлов

```
soa/
├── docker-compose.yaml              # Docker Compose конфигурация
├── keycloak-setup.sh               # Скрипт настройки Keycloak
├── init-scripts/init.sql           # SQL скрипты инициализации
├── backend-films/                  # JAX-RS/WildFly сервис
│   ├── src/main/java/com/blps/
│   │   ├── config/JwtAuthFilter.java
│   │   └── controller/MovieController.java
│   └── pom.xml
├── backend-oscars/                 # Spring Boot сервис
│   ├── src/main/java/com/jellyone/oscars/
│   │   ├── config/SecurityConfig.java
│   │   ├── service/JwtService.java
│   │   └── service/MoviesApiClient.java
│   └── build.gradle.kts
└── frontend/                       # Next.js приложение
    ├── lib/
    │   ├── oauth2-auth.ts
    │   └── api-client.ts
    ├── components/auth-component.tsx
    └── package.json
```

## 🎯 Ключевые особенности

### ✅ Реализовано
- **OAuth2/OIDC** аутентификация через Keycloak
- **JWT Token** валидация и генерация
- **Межсервисная** аутентификация
- **Role-based** авторизация
- **Stateless** архитектура
- **Docker** контейнеризация
- **Автоматическая** настройка

### 🔄 Потоки аутентификации
1. **Frontend → Keycloak**: Authorization Code Flow
2. **Backend Oscars → Backend Films**: JWT Token
3. **API Calls**: Bearer Token в заголовках
4. **Logout**: OAuth2 Logout Flow

## 🛠️ Технические детали

### Зависимости
- **Keycloak**: 26.4
- **Spring Security**: 6.2.0
- **JJWT**: 0.12.6
- **Next.js**: 14.2.25
- **WildFly**: 34.0.1.Final

### Конфигурация
- **Realm**: soa-realm
- **Clients**: backend-films, backend-oscars, frontend
- **Users**: testuser (test123)
- **Roles**: authenticated

## 🎉 Результат

Успешно реализована полнофункциональная система SSO с использованием Keycloak OAuth2/OIDC, обеспечивающая:
- **Единый вход** для всех сервисов
- **Безопасную** межсервисную коммуникацию
- **Масштабируемую** архитектуру
- **Простое** управление пользователями и ролями

Система готова к использованию и дальнейшему развитию!

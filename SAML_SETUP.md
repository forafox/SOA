# SOA Project - Keycloak SAML Integration

Этот проект настроен для работы с Keycloak SAML в качестве единой точки входа (SSO) для всех сервисов.

## Архитектура

- **Frontend**: Next.js с SAML аутентификацией
- **Backend Films**: JAX-RS с SAML интеграцией
- **Backend Oscars**: Spring Boot с SAML интеграцией
- **Keycloak**: Identity Provider с SAML поддержкой
- **PostgreSQL**: База данных

## Запуск проекта

### 1. Запуск всех сервисов

```bash
docker-compose up -d
```

### 2. Проверка статуса сервисов

```bash
docker-compose ps
```

### 3. Просмотр логов

```bash
# Все сервисы
docker-compose logs -f

# Конкретный сервис
docker-compose logs -f keycloak
docker-compose logs -f backend-films
docker-compose logs -f backend-oscars
docker-compose logs -f frontend
```

## Доступ к сервисам

- **Frontend**: http://localhost:3000
- **Backend Films**: http://localhost:8081
- **Backend Oscars**: http://localhost:8080
- **Keycloak Admin**: http://localhost:8082
  - Username: admin
  - Password: admin123

## SAML Конфигурация

### Keycloak Realm

Realm `soa-realm` автоматически создается при запуске с предустановленными:
- Пользователями: admin/admin123, testuser/test123
- Ролями: admin, user
- SAML клиентами для всех сервисов

### SAML Endpoints

- **Login**: `/realms/soa-realm/protocol/saml`
- **Logout**: `/realms/soa-realm/protocol/saml/logout`
- **Metadata**: `/realms/soa-realm/protocol/saml/descriptor`

### Backend Films (JAX-RS)

- **SAML Callback**: http://localhost:8081/saml/callback
- **SAML Logout**: http://localhost:8081/saml/logout
- **API**: http://localhost:8081/api/*

### Backend Oscars (Spring Boot)

- **SAML Login**: http://localhost:8080/saml/login
- **SAML Callback**: http://localhost:8080/saml/callback
- **SAML Logout**: http://localhost:8080/saml/logout
- **API**: http://localhost:8080/oscars/*

## Тестирование

### 1. Тест аутентификации

1. Откройте http://localhost:3000
2. Нажмите "Login with SAML"
3. Войдите с учетными данными:
   - Username: admin
   - Password: admin123
4. Проверьте, что вы перенаправлены обратно в приложение

### 2. Тест API

```bash
# Получение статуса аутентификации
curl http://localhost:8080/saml/status

# Тест backend-films (требует аутентификации)
curl http://localhost:8081/api/movies

# Тест backend-oscars (требует аутентификации)
curl http://localhost:8080/oscars/
```

## Устранение неполадок

### 1. Проблемы с SAML

- Проверьте логи Keycloak: `docker-compose logs keycloak`
- Убедитесь, что все сервисы запущены: `docker-compose ps`
- Проверьте конфигурацию SAML в Keycloak Admin Console

### 2. Проблемы с базой данных

- Проверьте подключение: `docker-compose logs postgres`
- Убедитесь, что init-db.sql выполнился корректно

### 3. Проблемы с сетью

- Проверьте, что все порты доступны
- Убедитесь, что нет конфликтов портов

## Конфигурация

### Environment Variables

#### Backend Films
- `KEYCLOAK_URL`: URL Keycloak (по умолчанию: http://keycloak:8080)
- `KEYCLOAK_REALM`: Realm (по умолчанию: soa-realm)
- `KEYCLOAK_CLIENT_ID`: Client ID (по умолчанию: backend-films)

#### Backend Oscars
- `KEYCLOAK_URL`: URL Keycloak (по умолчанию: http://keycloak:8080)
- `KEYCLOAK_REALM`: Realm (по умолчанию: soa-realm)
- `KEYCLOAK_CLIENT_ID`: Client ID (по умолчанию: backend-oscars)

#### Frontend
- `NEXT_PUBLIC_KEYCLOAK_URL`: URL Keycloak (по умолчанию: http://localhost:8082)
- `NEXT_PUBLIC_KEYCLOAK_REALM`: Realm (по умолчанию: soa-realm)
- `NEXT_PUBLIC_KEYCLOAK_CLIENT_ID`: Client ID (по умолчанию: frontend)

## Разработка

### Добавление новых ролей

1. Откройте Keycloak Admin Console
2. Перейдите в Realm > Roles
3. Создайте новую роль
4. Назначьте роль пользователям
5. Обновите конфигурацию SAML клиентов

### Добавление новых пользователей

1. Откройте Keycloak Admin Console
2. Перейдите в Users > Add User
3. Создайте пользователя
4. Установите пароль
5. Назначьте роли

## Безопасность

- Все SAML запросы должны быть подписаны
- Используйте HTTPS в продакшене
- Регулярно обновляйте пароли
- Мониторьте логи на предмет подозрительной активности

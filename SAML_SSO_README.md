# SAML SSO Integration для SOA Services

## Обзор

Этот проект интегрирует Keycloak SAML SSO для обеспечения единой аутентификации между всеми компонентами SOA архитектуры:

- **Frontend** (Next.js) - веб-интерфейс с SAML аутентификацией
- **Backend Films** (JAX-RS/WildFly) - сервис фильмов с SAML + JWT аутентификацией
- **Backend Oscars** (Spring Boot) - сервис Оскаров с SAML + JWT аутентификацией
- **Keycloak** - сервер аутентификации и авторизации

## Архитектура аутентификации

```
┌─────────────┐    SAML     ┌─────────────┐
│   Frontend  │ ──────────► │   Keycloak  │
└─────────────┘              └─────────────┘
       │                           │
       │ HTTP + SAML Headers       │ SAML
       ▼                           ▼
┌─────────────┐              ┌─────────────┐
│Backend Films│              │Backend Oscars│
└─────────────┘              └─────────────┘
       ▲                           │
       │ JWT Token                 │ JWT Token
       └───────────────────────────┘
```

## Быстрый старт

### 1. Запуск сервисов

```bash
# Запуск всех сервисов
docker-compose up -d

# Проверка статуса
docker-compose ps
```

### 2. Настройка Keycloak

```bash
# Автоматическая настройка SAML клиентов
./keycloak-setup.sh
```

### 3. Тестирование интеграции

```bash
# Запуск тестов
./test-saml-integration.sh
```

## Конфигурация

### Keycloak

- **URL**: http://localhost:8082
- **Admin**: admin / admin123!
- **Realm**: soa-realm
- **SAML Clients**: frontend, backend-films, backend-oscars

### Сервисы

- **Frontend**: http://localhost:3000
- **Backend Films**: http://localhost:8080
- **Backend Oscars**: http://localhost:8081
- **Keycloak**: http://localhost:8082

## Пользователи

### Тестовый пользователь
- **Username**: testuser
- **Password**: test123
- **Email**: test@example.com

## SAML Flow

### 1. Аутентификация пользователя

1. Пользователь заходит на фронтенд
2. Нажимает "Login with SAML SSO"
3. Перенаправляется на Keycloak
4. Вводит учетные данные
5. Keycloak возвращает SAML assertion
6. Фронтенд сохраняет информацию о пользователе

### 2. Доступ к API

1. Фронтенд отправляет запросы с SAML заголовками
2. Backend сервисы проверяют SAML аутентификацию
3. При необходимости межсервисных вызовов используется JWT

### 3. Межсервисная аутентификация

1. Backend Oscars генерирует JWT токен
2. Отправляет запросы к Backend Films с JWT
3. Backend Films валидирует JWT токен

## API Endpoints

### Frontend
- `GET /` - Главная страница с аутентификацией
- `GET /movies` - Список фильмов (требует аутентификации)
- `GET /oscars` - Операции с Оскарами (требует аутентификации)

### Backend Films (требует аутентификации)
- `GET /api/movies` - Получить список фильмов
- `POST /api/movies` - Создать фильм
- `GET /api/movies/{id}` - Получить фильм по ID
- `PATCH /api/movies/{id}` - Обновить фильм
- `DELETE /api/movies/{id}` - Удалить фильм

### Backend Oscars (требует аутентификации)
- `GET /oscars/operators/losers` - Получить список проигравших
- `POST /oscars/movies/honor-by-length/{minLength}` - Наградить фильмы по длине
- `POST /oscars/movies/honor-low-oscars` - Наградить фильмы с малым количеством Оскаров
- `GET /oscars/movies/{movieId}` - Получить Оскары фильма
- `POST /oscars/movies/{movieId}` - Добавить Оскары к фильму
- `DELETE /oscars/movies/{movieId}` - Удалить Оскары фильма

## Безопасность

### SAML Configuration
- Используется POST binding для безопасности
- Подпись assertions включена
- Валидация сертификатов настроена

### JWT Configuration
- HMAC SHA-256 подпись
- Время жизни токена: 1 час
- Секретный ключ для подписи

### CORS
- Настроен для всех сервисов
- Разрешены необходимые заголовки
- Поддержка credentials

## Troubleshooting

### Проблемы с аутентификацией

1. **401 Unauthorized**
   ```bash
   # Проверить статус Keycloak
   curl http://localhost:8082/realms/master
   
   # Проверить SAML метаданные
   curl http://localhost:8082/realms/soa-realm/protocol/saml/descriptor
   ```

2. **SAML ошибки**
   ```bash
   # Проверить логи Keycloak
   docker logs keycloak
   
   # Проверить конфигурацию SAML клиентов в админке
   # http://localhost:8082/admin
   ```

3. **Межсервисные ошибки**
   ```bash
   # Проверить логи backend-oscars
   docker logs backend-oscars
   
   # Проверить логи backend-films
   docker logs backend-films
   ```

### Полезные команды

```bash
# Перезапуск сервисов
docker-compose restart

# Просмотр логов
docker-compose logs -f keycloak
docker-compose logs -f backend-films
docker-compose logs -f backend-oscars
docker-compose logs -f frontend

# Очистка и пересборка
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## Разработка

### Добавление новых ролей

1. В Keycloak админке добавить роль в realm
2. Назначить роль пользователю
3. Обновить `role-mappings.properties` в backend-films
4. Обновить проверки ролей в коде

### Добавление новых сервисов

1. Создать SAML клиент в Keycloak
2. Настроить SAML конфигурацию в сервисе
3. Добавить JWT поддержку для межсервисных вызовов
4. Обновить тесты

## Мониторинг

### Health Checks
- Keycloak: http://localhost:8082/realms/master
- Backend Oscars: http://localhost:8081/actuator/health
- Frontend: http://localhost:3000

### Логи
- Все сервисы логируют аутентификационные события
- SAML assertions логируются в Keycloak
- JWT токены логируются в backend сервисах

## Лицензия

MIT License

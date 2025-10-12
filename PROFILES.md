# SOA Project - Профили запуска

Этот проект поддерживает два профиля запуска для разных окружений.

## 🐳 Docker профиль (локальная разработка)

Для локальной разработки с Docker контейнерами:

```bash
./start-docker.sh
```

**Конфигурация:**
- Фронтенд: `http://localhost:3000`
- Movies API: `http://localhost:8080`
- Oscars API: `http://localhost:8081`
- PostgreSQL: `localhost:5433`
- Keycloak: `http://localhost:8082`
- Base Path: `/` (без префикса)

**Переменные окружения:**
- `NODE_ENV=production`
- `NEXT_PUBLIC_MOVIES_API_URL=http://localhost:8080`
- `NEXT_PUBLIC_OSCARS_API_URL=http://localhost:8081`
- `NEXT_PUBLIC_BASE_PATH=""`

## 🌐 Helios профиль (продакшн с внешними API)

Для запуска с внешними API на сервере Helios:

```bash
./start-helios.sh
```

**Конфигурация:**
- Фронтенд: `http://localhost:3000`
- Movies API: `https://se.ifmo.ru/~s367268/movies-api`
- Oscars API: `https://se.ifmo.ru/~s367268/oscars-api`
- PostgreSQL: `localhost:5433`
- Keycloak: `http://localhost:8082`
- Base Path: `/~s367268/soa`

**Переменные окружения:**
- `NODE_ENV=production`
- `NEXT_PUBLIC_MOVIES_API_URL=https://se.ifmo.ru/~s367268/movies-api`
- `NEXT_PUBLIC_OSCARS_API_URL=https://se.ifmo.ru/~s367268/oscars-api`
- `NEXT_PUBLIC_BASE_PATH=/~s367268/soa`

## 📁 Файлы конфигурации

- `docker-compose.yaml` - Docker профиль
- `docker-compose.helios.yaml` - Helios профиль
- `start-docker.sh` - Скрипт запуска Docker профиля
- `start-helios.sh` - Скрипт запуска Helios профиля

## 🔧 Ручная настройка

Если нужно изменить конфигурацию вручную:

```bash
# Docker профиль
docker-compose up -d

# Helios профиль
docker-compose -f docker-compose.helios.yaml up -d
```

## 🛑 Остановка

```bash
docker-compose down --volumes --remove-orphans
```

## 📝 Примечания

- Все профили используют одинаковые backend контейнеры
- Различается только конфигурация фронтенда
- Helios профиль использует внешние API вместо локальных
- Base Path настраивается автоматически в зависимости от профиля

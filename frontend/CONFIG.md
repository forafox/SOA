# 🔧 Конфигурация адресов бэкенда

Система для удобного управления адресами бэкенда в разных окружениях.

## 📋 Доступные конфигурации

### 1. Локальная разработка (по умолчанию)
- **Movies API**: `http://localhost:8081`
- **Oscars API**: `http://localhost:8080`

### 2. Продакшн на сервере
- **Movies API**: `https://se.ifmo.ru/~s367268/movies-api`
- **Oscars API**: `https://se.ifmo.ru/~s367268/oscars-api`

## 🚀 Способы использования

### Локальная разработка

#### Способ 1: Автоматический (рекомендуется)
```bash
# Запуск с настройками по умолчанию
./start-local.sh

# Запуск с кастомными адресами
./start-local.sh http://localhost:8081 http://localhost:8080
```

#### Способ 2: Ручной
```bash
# Установка переменных окружения
export NEXT_PUBLIC_MOVIES_API_URL=http://localhost:8081
export NEXT_PUBLIC_OSCARS_API_URL=http://localhost:8080

# Запуск сервера
npm run dev
```

### Деплой на сервер

#### Способ 1: Автоматический (рекомендуется)
```bash
# Деплой с настройками по умолчанию
./deploy-working.sh helios

# Деплой с кастомными адресами
./deploy-working.sh helios https://se.ifmo.ru/~s367268/movies-api https://se.ifmo.ru/~s367268/oscars-api
```

#### Способ 2: Ручной
```bash
# Установка переменных окружения
export NEXT_PUBLIC_MOVIES_API_URL=https://se.ifmo.ru/~s367268/movies-api
export NEXT_PUBLIC_OSCARS_API_URL=https://se.ifmo.ru/~s367268/oscars-api
export NODE_ENV=production

# Сборка и деплой
npm run build
rsync -av --delete out/ helios:~/public_html/soa/
```

## ⚙️ Файлы конфигурации

### `lib/config.ts`
Основной файл конфигурации с логикой определения окружения.

### `config.local.js`
Конфигурация для локальной разработки.

### `config.production.js`
Конфигурация для продакшена.

## 🔄 Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `NEXT_PUBLIC_MOVIES_API_URL` | URL Movies API | `http://localhost:8081` |
| `NEXT_PUBLIC_OSCARS_API_URL` | URL Oscars API | `http://localhost:8080` |
| `NODE_ENV` | Окружение | `development` |

## 📝 Примеры использования

### Разработка с кастомными портами
```bash
./start-local.sh http://localhost:9001 http://localhost:9000
```

### Деплой на тестовый сервер
```bash
./deploy-working.sh test-server https://test.example.com/movies https://test.example.com/oscars
```

### Использование в коде
```typescript
import { backendConfig, callbackUrls } from './lib/config'

// Получение текущей конфигурации
console.log('Movies API:', backendConfig.moviesApiUrl)
console.log('Oscars API:', backendConfig.oscarsApiUrl)

// Получение URL callback'ов
console.log('Callback URLs:', callbackUrls)
```

## 🛠️ Настройка для новых окружений

1. Добавьте новую конфигурацию в `lib/config.ts`:
```typescript
const configs: Record<string, BackendConfig> = {
  // ... существующие конфигурации
  staging: {
    moviesApiUrl: 'https://staging.example.com/movies',
    oscarsApiUrl: 'https://staging.example.com/oscars',
    environment: 'production'
  }
}
```

2. Создайте скрипт для нового окружения:
```bash
#!/bin/bash
export NEXT_PUBLIC_MOVIES_API_URL=https://staging.example.com/movies
export NEXT_PUBLIC_OSCARS_API_URL=https://staging.example.com/oscars
export NODE_ENV=production
npm run build
# ... деплой
```

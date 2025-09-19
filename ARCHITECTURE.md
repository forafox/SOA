# 🏗️ Архитектура приложения после интеграции

## 📊 Схема работы

```
┌─────────────────────────────────────────────────────────────┐
│                    Гелиос (WildFly)                        │
│                    Порт: 8080                              │
└─────────────────────────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
        ┌───────▼───────┐ ┌─────▼─────┐ ┌──────▼──────┐
        │   Фронтенд    │ │   API     │ │   Прокси    │
        │  (Next.js)    │ │ (Oscars)  │ │  (Movies)   │
        │               │ │           │ │             │
        │ /             │ │ /oscars/* │ │ /api/movies │
        │ /movies       │ │           │ │     ↓       │
        │ /oscars       │ │           │ │  Проксирует │
        │ /dashboard    │ │           │ │  к порту    │
        └───────────────┘ └───────────┘ │   8081      │
                                        └─────────────┘
```

## 🔄 Поток запросов

### 1. Статические файлы (фронтенд)
```
Браузер → http://domain:8080/ 
       → Spring Boot → static/index.html
```

### 2. API Oscars (локальный)
```
Браузер → http://domain:8080/oscars/operators/losers
       → Spring Boot → OscarsController
```

### 3. API Movies (прокси)
```
Браузер → http://domain:8080/api/movies
       → Spring Boot → MoviesProxyController
       → Проксирует → http://localhost:8081/movies
```

## ⚙️ Компоненты

### Frontend (Next.js)
- **Расположение**: `src/main/resources/static/`
- **Маршруты**: `/`, `/movies`, `/oscars`, `/dashboard`
- **API вызовы**: Все идут на порт 8080

### Backend (Spring Boot)
- **Порт**: 8080
- **Контроллеры**:
  - `OscarsController` - `/oscars/*`
  - `MoviesProxyController` - `/api/movies/*` (прокси)

### Прокси для Movies API
- **Назначение**: Перенаправляет запросы к movies API
- **Конфигурация**: `movies.api.base-url=http://localhost:8081/api`
- **Методы**: GET, POST, PATCH, DELETE

## 🎯 Преимущества

1. **Единый порт**: Все запросы идут на 8080
2. **Нет CORS**: Один домен для фронтенда и API
3. **Простой деплой**: Один WAR файл
4. **Прозрачность**: Фронтенд не знает о проксировании

## ⚠️ Важные моменты

### Конфигурация статических ресурсов
```java
// Исключаем API endpoints из обработки статики
if (resourcePath.startsWith("oscars/") || 
    resourcePath.startsWith("swagger-ui") || 
    resourcePath.startsWith("v3/api-docs")) {
    return null; // Не обрабатываем как статику
}
```

### Проксирование Movies API
```java
// Все запросы /api/movies/* проксируются к movies API
String url = moviesApiBaseUrl + "/movies" + path;
return restTemplate.exchange(url, method, entity, Object.class);
```

## 🔧 Настройки

### application.yaml
```yaml
movies:
  api:
    base-url: http://localhost:8081/api  # URL movies API
```

### Frontend API Client
```typescript
// Оба API теперь используют порт 8080
constructor(moviesBaseUrl = "http://localhost:8080", 
           oscarsBaseUrl = "http://localhost:8080")
```

## 🚀 Результат

После деплоя на Гелиос:
- ✅ Фронтенд доступен на `http://domain:8080/`
- ✅ Oscars API работает на `http://domain:8080/oscars/*`
- ✅ Movies API проксируется через `http://domain:8080/api/movies/*`
- ✅ Все запросы идут через один порт
- ✅ Нет проблем с CORS
- ✅ Один WAR файл содержит всё

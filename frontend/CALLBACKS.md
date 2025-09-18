# Система коллбэков для Оскар-сервиса

## Обзор

Система коллбэков позволяет фронтенду получать уведомления от бэкенда Оскар-сервиса о различных событиях, связанных с награждением фильмов.

## Типы коллбэков

### 1. onAwarded
- **Назначение**: Уведомление о награждении фильмов по длине
- **Endpoint**: `/api/callbacks/on-awarded`
- **Триггер**: При вызове `honorMoviesByLength`
- **Данные**: 
  ```json
  {
    "movieId": 1,
    "newOscarsCount": 5,
    "updatedMovies": [...]
  }
  ```

### 2. notifyAdmins
- **Назначение**: Уведомление администраторов о награждении фильмов с малым количеством Оскаров
- **Endpoint**: `/api/callbacks/notify-admins`
- **Триггер**: При вызове `honorMoviesWithFewOscars`
- **Данные**:
  ```json
  {
    "movieId": 2,
    "addedOscars": 2,
    "updatedMovies": [...]
  }
  ```

### 3. notifyOscarsTeam
- **Назначение**: Уведомление команды Оскаров о добавлении Оскаров к конкретному фильму
- **Endpoint**: `/api/callbacks/notify-oscars-team`
- **Триггер**: При вызове `addOscarsToMovie`
- **Данные**:
  ```json
  {
    "movieId": 3,
    "category": "UPDATE",
    "date": "2024-01-01",
    "addedOscars": 1
  }
  ```

## Архитектура

### Компоненты

1. **CallbackService** (`/lib/callback-service.ts`)
   - Singleton сервис для обработки коллбэков
   - Отправляет toast уведомления
   - Генерирует события для UI

2. **API Routes** (`/app/api/callbacks/`)
   - Принимают POST запросы от бэкенда
   - Обрабатывают данные через CallbackService
   - Возвращают статус обработки

3. **CallbackStatus** (`/components/oscars/callback-status.tsx`)
   - Отображает историю коллбэков
   - Показывает статус обработки
   - Сворачиваемый интерфейс

4. **CallbackTester** (`/components/oscars/callback-tester.tsx`)
   - Инструмент для тестирования коллбэков
   - Позволяет отправить тестовые данные
   - Полезен для разработки и отладки

### Поток данных

```
Бэкенд Оскар-сервиса
    ↓ POST с callbackUrl
API Route (/api/callbacks/*)
    ↓ Обработка данных
CallbackService
    ↓ Toast + CustomEvent
UI Компоненты
    ↓ Отображение
CallbackStatus
```

## Использование

### Автоматическое использование

Коллбэки автоматически включаются при вызове соответствующих API методов:

```typescript
// Автоматически включает коллбэк onAwarded
await apiClient.honorMoviesByLength(120, 2)

// Автоматически включает коллбэк notifyAdmins  
await apiClient.honorMoviesWithFewOscars(3, 1)

// Автоматически включает коллбэк notifyOscarsTeam
await apiClient.addOscarsToMovie(1, 2)
```

### Ручное тестирование

Используйте компонент `CallbackTester` на странице Оскаров для отправки тестовых коллбэков.

### Мониторинг

Компонент `CallbackStatus` показывает:
- Историю последних 10 коллбэков
- Статус обработки (успех/ошибка/ожидание)
- Время получения
- Детали каждого коллбэка

## Настройка

### URL коллбэков

URL коллбэков генерируются автоматически на основе текущего домена:

```typescript
const urls = callbackService.getCallbackUrls()
// {
//   onAwarded: "http://localhost:3000/api/callbacks/on-awarded",
//   notifyAdmins: "http://localhost:3000/api/callbacks/notify-admins", 
//   notifyOscarsTeam: "http://localhost:3000/api/callbacks/notify-oscars-team"
// }
```

### Обработка ошибок

- Ошибки коллбэков не влияют на основную логику
- Все ошибки логируются в консоль
- UI показывает toast уведомления об ошибках

## Разработка

### Добавление нового типа коллбэка

1. Создайте новый API route в `/app/api/callbacks/`
2. Добавьте метод в `CallbackService`
3. Обновите `CallbackStatus` для отображения нового типа
4. Добавьте тест в `CallbackTester`

### Отладка

1. Откройте DevTools → Console для просмотра логов
2. Используйте `CallbackTester` для тестирования
3. Проверьте `CallbackStatus` для истории событий
4. Мониторьте Network tab для HTTP запросов

## Безопасность

- Коллбэки принимают только POST запросы
- Валидация данных на уровне API routes
- Обработка ошибок без раскрытия внутренней логики
- Логирование всех входящих коллбэков

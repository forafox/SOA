# Инструкция по запуску приложений

## Порты приложений

- **Frontend (Next.js)**: http://localhost:3000
- **Backend Films (Jersey)**: http://localhost:8081/api/
- **Backend Oscars (Spring Boot)**: http://localhost:8080

## Порядок запуска

### 1. Запуск Backend Films (порт 8081)
```bash
cd backend-films
mvn clean compile
mvn exec:java -Dexec.mainClass="com.blps.Main"
```

### 2. Запуск Backend Oscars (порт 8080)
```bash
cd backend-oscars
./gradlew bootRun
```

### 3. Запуск Frontend (порт 3000)
```bash
cd frontend
npm run dev
```

## Исправленные проблемы

1. **Конфликт портов**: Backend Films перенесен с порта 8080 на 8081
2. **CORS настройки**: Добавлены CORS заголовки в оба бэкенда
3. **API клиент**: Обновлен для работы с правильными портами

## Проверка работы

После запуска всех приложений:
1. Откройте http://localhost:3000
2. Переключите "Use Mock Data" в выключенное состояние
3. Попробуйте загрузить список фильмов

API должно работать без CORS ошибок.

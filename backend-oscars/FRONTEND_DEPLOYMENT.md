# Деплой фронтенда через Spring приложение

## ✅ Настройка завершена

Ваше Spring приложение теперь настроено для обслуживания статического фронтенда вместе с API.

## 📦 Сборка WAR файла с фронтендом

### Автоматическая сборка
```bash
./gradlew clean build
```

Эта команда автоматически:
1. Собирает фронтенд в production режиме
2. Копирует статические файлы в `src/main/resources/static/`
3. Упаковывает все в WAR файл

### Ручная сборка (альтернатива)
```bash
# Только фронтенд
./gradlew buildFrontend

# Полная сборка
./gradlew clean build
```

## 🚀 Результат

**WAR файл:** `build/libs/backend-oscars-0.0.1-SNAPSHOT.war` (размер: ~33MB)

## 🌐 Адреса доступа после деплоя

### При локальном запуске (standalone)
- **Фронтенд:** http://localhost:8080/
- **API Oscars:** http://localhost:8080/oscars/
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html

### При деплое в WildFly (с context path)
- **Фронтенд:** http://your-server:8132/backend-oscars-0.0.1-SNAPSHOT/
- **API Oscars:** http://your-server:8132/backend-oscars-0.0.1-SNAPSHOT/oscars/
- **Swagger UI:** http://your-server:8132/backend-oscars-0.0.1-SNAPSHOT/swagger-ui/index.html

## 📋 Маршруты фронтенда

Фронтенд (SPA) доступен по следующим маршрутам:
- `/` - Главная страница с дашбордом
- `/movies` - Управление фильмами  
- `/oscars` - Управление наградами Оскаров
- `/dashboard` - Статистика и аналитика

## 🔧 Конфигурация

### Автоматическая настройка
- ✅ Static resources служатся из `/static/`
- ✅ SPA routing настроен (все неизвестные маршруты → index.html)
- ✅ API маршруты исключены из SPA routing
- ✅ Кэширование настроено (статика - 1 год, HTML - без кэша)

### Исключения из SPA routing
- `/api/**` - API endpoints
- `/oscars/**` - Oscars API
- `/swagger-ui/**` - Swagger UI  
- `/v3/api-docs/**` - OpenAPI docs
- `/_next/**` - Next.js статика

## 📝 Файлы, которые были изменены

1. **build.gradle.kts** - добавлена задача `buildFrontend`
2. **application.yaml** - настройка static resources
3. **application-wildfly.yaml** - настройка для WildFly
4. **StaticResourceConfig.java** - конфигурация маршрутизации
5. **FrontendController.java** - контроллер для SPA
6. **frontend/lib/config.ts** - конфигурация для embedded режима

## 🚀 Инструкции по деплою

### WildFly
1. Скопируйте `backend-oscars-0.0.1-SNAPSHOT.war` в `deployments/`
2. Фронтенд будет доступен по context path приложения

### Tomcat  
1. Скопируйте WAR файл в `webapps/`
2. Переименуйте в `ROOT.war` для доступа по корневому пути

### Standalone (встроенный Tomcat)
```bash
java -jar backend-oscars-0.0.1-SNAPSHOT.war
```

## ⚡ Производительность

- **Размер WAR:** ~33MB (включая все зависимости + фронтенд)
- **Кэширование:** Статические ресурсы кэшируются на 1 год
- **Компрессия:** Next.js оптимизация включена
- **Lazy loading:** Next.js автоматически разбивает код

## 🐛 Отладка

Если фронтенд не отображается:
1. Проверьте наличие файлов в WAR: `unzip -l backend-oscars-0.0.1-SNAPSHOT.war | grep static`
2. Проверьте логи Spring на предмет ошибок static resources
3. Убедитесь, что context path настроен правильно

## 📚 Дополнительно

- Frontend собирается в production режиме с оптимизациями
- Поддерживается Hot Reload при разработке (используйте `npm run dev` в папке frontend)
- API Movies остается внешним сервисом и настраивается через конфигурацию

# 🚀 Гайд по деплою фронтенда и бэкенда на Гелиос (WildFly)

## 📋 Обзор

Этот гайд описывает процесс интеграции Next.js фронтенда с Spring Boot бэкендом и деплоя на Гелиос через WildFly.

## 🏗️ Архитектура

- **Фронтенд**: Next.js приложение, собирается в статические файлы
- **Бэкенд**: Spring Boot приложение с интегрированным фронтендом
- **Сервер приложений**: WildFly (настраивается администратором)
- **Статические ресурсы**: Размещаются в `src/main/resources/static/`
- **Порт**: 8080 (единый для фронтенда и API)

### 🔄 Как это работает

1. **Фронтенд** отдается как статические файлы с корневого URL (`/`)
2. **Oscars API** работает напрямую (`/oscars/*`)
3. **Movies API** проксируется через Spring Boot (`/api/movies/*` → `http://localhost:8081/movies/*`)
4. **Все запросы** идут через один порт 8080, нет проблем с CORS

## 🔧 Локальная разработка

### 1. Сборка фронтенда и интеграция с бэкендом

#### Вариант 1: Автоматический скрипт
```bash
# Из корневой директории проекта
./build-frontend.sh
```

#### Вариант 2: Gradle задача
```bash
# Из директории backend-oscars
cd backend-oscars
./gradlew buildFrontend
```

#### Вариант 3: Ручная сборка
```bash
# 1. Собираем фронтенд
cd frontend
npm install
npm run build

# 2. Копируем в Spring Boot
rm -rf ../backend-oscars/src/main/resources/static/*
cp -r out/* ../backend-oscars/src/main/resources/static/
```

### 2. Запуск приложения

```bash
cd backend-oscars
./gradlew bootRun
```

Приложение будет доступно по адресу: `http://localhost:8080`

## 🌐 Деплой на Гелиос (WildFly)

### 1. Подготовка к деплою

1. **Убедитесь, что фронтенд собран и интегрирован:**
   ```bash
   ./build-frontend.sh
   ```

2. **Проверьте, что статические файлы на месте:**
   ```bash
   ls -la backend-oscars/src/main/resources/static/
   ```

### 2. Сборка WAR файла

```bash
cd backend-oscars
./gradlew war
```

WAR файл будет создан в: `build/libs/backend-oscars-0.0.1-SNAPSHOT.war`

### 3. Деплой на Гелиос

1. **Загрузите WAR файл на Гелиос**
2. **Разверните WAR файл в WildFly** (настраивается администратором)
3. **Убедитесь, что порт 8080 доступен**

### 4. Проверка деплоя

После успешного деплоя:
- Фронтенд будет доступен по корневому URL: `http://your-helios-domain:8080/`
- API endpoints будут доступны по: `http://your-helios-domain:8080/api/`

### 5. Структура WAR файла

WAR файл содержит:
```
backend-oscars-0.0.1-SNAPSHOT.war
├── WEB-INF/
│   ├── classes/
│   │   ├── com/jellyone/oscars/     # Java классы
│   │   └── static/                  # ← Статические файлы фронтенда
│   │       ├── index.html
│   │       ├── _next/
│   │       └── ...
│   └── lib/                         # Зависимости
└── META-INF/
```

## 🔧 Конфигурация

### Spring Boot конфигурация

Статические ресурсы настроены в `CorsConfig.java`:

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Настройка для статических ресурсов фронтенда
    registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600);
    
    // Настройка для API endpoints
    registry.addResourceHandler("/api/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(0);
}
```

### Next.js конфигурация

В `next.config.mjs` настроен статический экспорт:

```javascript
const nextConfig = {
  output: 'export',
  trailingSlash: true,
  images: {
    unoptimized: true,
  },
  // ... другие настройки
};
```

## 🚨 Устранение проблем

### Проблема: Фронтенд не отображается
**Решение:**
1. Проверьте, что статические файлы скопированы в `static/`
2. Убедитесь, что Spring Boot запущен на порту 8080
3. Проверьте конфигурацию `addResourceHandlers`

### Проблема: API не работает
**Решение:**
1. Убедитесь, что API endpoints имеют префикс `/api/`
2. Проверьте CORS настройки
3. Проверьте логи Spring Boot

### Проблема: Ошибки сборки фронтенда
**Решение:**
1. Убедитесь, что установлены все зависимости: `npm install`
2. Проверьте конфигурацию Next.js
3. Очистите кэш: `rm -rf .next out`

## 📁 Структура файлов после интеграции

```
backend-oscars/
├── src/main/resources/
│   └── static/                 # ← Собранный фронтенд здесь
│       ├── index.html
│       ├── _next/
│       └── ...
├── build/libs/
│   └── backend-oscars-0.0.1-SNAPSHOT.war
└── ...
```

## 🎯 Преимущества такой архитектуры

1. **Единый порт**: Фронтенд и API на одном порту
2. **Простой деплой**: Один WAR файл содержит всё
3. **CORS не нужен**: Нет межпортовых запросов
4. **Производительность**: Статические файлы кэшируются
5. **Безопасность**: Нет разделения на разные домены

## 🔄 Обновление фронтенда

При изменении фронтенда:

1. Соберите фронтенд: `./build-frontend.sh`
2. Пересоберите WAR: `cd backend-oscars && ./gradlew war`
3. Передеплойте на Гелиос

---

**Готово!** 🎉 Теперь у вас есть полностью интегрированное приложение, готовое к деплою на Гелиос.

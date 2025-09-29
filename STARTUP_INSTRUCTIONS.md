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

## helios

film service
```bash
cd wildfly{version}/bin
./standalone.sh -c standalone-films.xml
```
oscar service
```bash
./standalone.sh -c standalone-oscars.xml
```
Добавить .war в Deployment
film service ports:
```bash
http: 37860
https: 37861
management-http: 38860
```

oscar service ports:
```bash
http: 37863
https: 37862
management-http: 38862
```
Management console film service
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L38860:helios:38860
```

Management console oscar service
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L38862:helios:38862
```

film service api
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L37860:helios:37860
```

oscar service api
```bash
ssh -p 2222 s{student_number}@se.ifmo.ru -Y -L37863:helios:37863
```

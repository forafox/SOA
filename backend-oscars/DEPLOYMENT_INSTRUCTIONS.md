# Инструкция по развертыванию на удаленном сервере

## Файлы для передачи на сервер:

1. **backend-oscars-0.0.1-SNAPSHOT.war** - WAR файл приложения
2. **standalone-oscars-minimal.xml** - конфигурация WildFly

## Шаги развертывания:

### 1. Скопируйте файлы на сервер:
```bash
# Скопируйте WAR файл в папку deployments
cp backend-oscars-0.0.1-SNAPSHOT.war /path/to/wildfly/standalone/deployments/

# Скопируйте конфигурацию в папку configuration
cp standalone-oscars-minimal.xml /path/to/wildfly/standalone/configuration/
```

### 2. Запустите WildFly:
```bash
cd /path/to/wildfly/bin
sh standalone.sh -c standalone-oscars-minimal.xml
```

### 3. Проверьте доступность:
- **Приложение**: http://server:8132/backend-oscars-0.0.1-SNAPSHOT/
- **Swagger UI**: http://server:8132/backend-oscars-0.0.1-SNAPSHOT/swagger-ui.html
- **Management Console**: http://server:8134

## Порты:
- **HTTP**: 8132
- **HTTPS**: 8133  
- **Management**: 8134
- **Management HTTPS**: 8135

## Возможные проблемы:

### Если приложение не запускается:
1. Проверьте логи: `tail -f /path/to/wildfly/standalone/log/server.log`
2. Убедитесь, что порты свободны: `netstat -an | grep 813`
3. Проверьте права доступа к файлам

### Если есть ошибки логирования:
- Конфигурация уже оптимизирована для работы с WildFly
- Logback исключен из зависимостей
- Используется WildFly's JBoss LogManager

## Структура файлов на сервере:
```
wildfly/
├── standalone/
│   ├── deployments/
│   │   └── backend-oscars-0.0.1-SNAPSHOT.war
│   ├── configuration/
│   │   └── standalone-oscars-minimal.xml
│   └── log/
│       └── server.log
└── bin/
    └── standalone.sh
```

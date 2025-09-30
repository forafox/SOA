#!/bin/bash

echo "=== Тестирование конфигурации WildFly ==="

# Проверяем, что WildFly существует
if [ ! -d "/home/studs/s367268/wildfly-37.0.1.Final" ]; then
    echo "❌ WildFly не найден в /home/studs/s367268/wildfly-37.0.1.Final"
    exit 1
fi

echo "✅ WildFly найден"

# Проверяем права доступа
if [ ! -x "/home/studs/s367268/wildfly-37.0.1.Final/bin/standalone.sh" ]; then
    echo "❌ Нет прав на выполнение standalone.sh"
    exit 1
fi

echo "✅ Права на выполнение есть"

# Проверяем конфигурационный файл
if [ ! -f "standalone-oscars-minimal.xml" ]; then
    echo "❌ Конфигурационный файл не найден"
    exit 1
fi

echo "✅ Конфигурационный файл найден"

# Проверяем WAR файл
if [ ! -f "build/libs/backend-oscars-0.0.1-SNAPSHOT.war" ]; then
    echo "❌ WAR файл не найден, собираем..."
    ./gradlew clean war
    if [ $? -ne 0 ]; then
        echo "❌ Ошибка сборки WAR"
        exit 1
    fi
fi

echo "✅ WAR файл готов"

# Копируем WAR в deployments
cp build/libs/backend-oscars-0.0.1-SNAPSHOT.war /home/studs/s367268/wildfly-37.0.1.Final/standalone/deployments/

echo "✅ WAR скопирован в deployments"

echo "=== Все проверки пройдены, можно запускать WildFly ==="
echo "Команда для запуска:"
echo "cd /home/studs/s367268/wildfly-37.0.1.Final/bin && sh standalone.sh -c standalone-oscars-minimal.xml"

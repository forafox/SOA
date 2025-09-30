#!/bin/bash

echo "=== Запуск WildFly с исправленной конфигурацией ==="

# Копируем WAR файл в deployments
echo "Копируем WAR файл..."
cp build/libs/backend-oscars-0.0.1-SNAPSHOT.war /home/studs/s367268/wildfly-37.0.1.Final/standalone/deployments/

# Копируем конфигурацию
echo "Копируем конфигурацию..."
cp standalone-oscars-minimal.xml /home/studs/s367268/wildfly-37.0.1.Final/standalone/configuration/

echo "✅ Файлы скопированы"
echo ""
echo "Теперь запустите WildFly:"
echo "cd /home/studs/s367268/wildfly-37.0.1.Final/bin"
echo "sh standalone.sh -c standalone-oscars-minimal.xml"
echo ""
echo "После запуска приложение будет доступно по адресу:"
echo "http://localhost:8132/backend-oscars-0.0.1-SNAPSHOT/"
echo "http://localhost:8132/backend-oscars-0.0.1-SNAPSHOT/swagger-ui.html"

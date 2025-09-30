#!/bin/bash

echo "=== Тестирование WildFly с исправленной конфигурацией ==="

# Остановить все процессы WildFly
echo "Остановка существующих процессов WildFly..."
pkill -f wildfly 2>/dev/null || true
sleep 3

# Проверить, что порты свободны
echo "Проверка портов..."
netstat -tuln | grep -E ":(9991|9993|8132|8133)" || echo "Порты свободны"

# Установить переменные окружения
export JBOSS_HOME=/home/studs/s367268/wildfly-37.0.1.Final
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Перейти в директорию WildFly
cd $JBOSS_HOME/bin

echo "Запуск WildFly с исправленной конфигурацией..."
echo "Management Console будет доступна на: http://localhost:9991"
echo "Приложение будет доступно на: http://localhost:8132"
echo "HTTPS будет доступен на: https://localhost:8133"

# Запустить WildFly
./standalone.sh -c standalone-oscars-minimal.xml -b 0.0.0.0 -bmanagement 0.0.0.0
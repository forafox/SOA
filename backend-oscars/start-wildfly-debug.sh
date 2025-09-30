#!/bin/bash

# Скрипт для запуска WildFly с отладкой
export JBOSS_HOME="/home/studs/s367268/wildfly-37.0.1.Final"
export JAVA_OPTS="-Xms128m -Xmx1g -Djava.net.preferIPv4Stack=true"

echo "Запуск WildFly с отладкой..."
echo "JBOSS_HOME: $JBOSS_HOME"
echo "JAVA_OPTS: $JAVA_OPTS"

# Запуск с подробным логированием
$JBOSS_HOME/bin/standalone.sh \
    -c standalone-oscars-minimal.xml \
    --debug \
    -Djboss.bind.address=0.0.0.0 \
    -Djboss.bind.address.management=0.0.0.0 \
    -Djboss.http.port=8132 \
    -Djboss.https.port=8133 \
    -Djboss.management.http.port=8134 \
    -Djboss.management.https.port=8135

#!/bin/bash

# Скрипт для запуска Helios профиля (продакшн с внешними API)
echo "🌐 Запуск Helios профиля (продакшн с внешними API)..."

# Останавливаем все контейнеры
docker-compose down --volumes --remove-orphans

# Пересобираем и запускаем с Helios конфигурацией
docker-compose -f docker-compose.helios.yaml build --no-cache
docker-compose -f docker-compose.helios.yaml up -d

echo "✅ Helios профиль запущен!"
echo "🌐 Фронтенд: http://localhost:3000"
echo "🎬 Movies API: https://se.ifmo.ru/~s367268/movies-api"
echo "🏆 Oscars API: https://se.ifmo.ru/~s367268/oscars-api"
echo "🗄️  PostgreSQL: localhost:5433"
echo "🔐 Keycloak: http://localhost:8082"

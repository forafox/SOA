#!/bin/bash

# Скрипт для запуска Docker профиля (локальная разработка)
echo "🐳 Запуск Docker профиля (локальная разработка)..."

# Останавливаем все контейнеры
docker-compose down --volumes --remove-orphans

# Пересобираем и запускаем
docker-compose build --no-cache
docker-compose up -d

echo "✅ Docker профиль запущен!"
echo "🌐 Фронтенд: http://localhost:3000"
echo "🎬 Movies API: http://localhost:8080"
echo "🏆 Oscars API: http://localhost:8081"
echo "🗄️  PostgreSQL: localhost:5433"
echo "🔐 Keycloak: http://localhost:8082"

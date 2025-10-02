#!/bin/bash

# Скрипт для сборки backend с встроенным frontend

set -e  # Останавливаться при ошибках

echo "🚀 Начинаем сборку backend с встроенным frontend..."

# Путь к фронтенду
FRONTEND_PATH="../frontend"
BACKEND_PATH="$(pwd)"

# Проверяем что фронтенд существует
if [ ! -d "$FRONTEND_PATH" ]; then
    echo "❌ Ошибка: Директория фронтенда не найдена: $FRONTEND_PATH"
    exit 1
fi

echo "📦 Собираем фронтенд в встроенном режиме..."
cd "$FRONTEND_PATH"

# Устанавливаем зависимости если нужно
if [ ! -d "node_modules" ]; then
    echo "📥 Устанавливаем зависимости фронтенда..."
    npm install
fi

# Собираем фронтенд в встроенном режиме
NEXT_PUBLIC_EMBEDDED_MODE=true NODE_ENV=production npm run build

echo "📁 Копируем статику в backend..."
cd "$BACKEND_PATH"

# Очищаем старую статику и создаем директорию
rm -rf src/main/resources/static
mkdir -p src/main/resources/static

# Копируем новую статику
cp -r "$FRONTEND_PATH/out/"* src/main/resources/static/

echo "🔨 Собираем war-файл..."
cd "$BACKEND_PATH"
./gradlew clean build

echo "✅ Сборка завершена успешно!"
echo "📦 WAR-файл: build/libs/backend-oscars-0.0.1-SNAPSHOT.war"
echo "📏 Размер: $(ls -lh build/libs/backend-oscars-0.0.1-SNAPSHOT.war | awk '{print $5}')"

echo ""
echo "🚀 Для деплоя скопируйте WAR-файл на сервер и разверните в WildFly/Tomcat"
echo "🌐 Фронтенд будет доступен по корневому пути сервера"
echo "🔗 API будет доступно по путям /api/* и /oscars/*"

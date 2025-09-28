#!/bin/bash

# Скрипт для запуска локальной разработки
# Использование: ./start-local.sh [movies_api_url] [oscars_api_url]

# Получаем URL API (опционально)
MOVIES_API_URL="$1"
OSCARS_API_URL="$2"

echo "🚀 Запуск локальной разработки..."

# Проверяем, что мы в правильной директории
if [ ! -f "package.json" ]; then
    echo "❌ Ошибка: package.json не найден. Запустите скрипт из корня проекта."
    exit 1
fi

# Устанавливаем переменные окружения для локальной разработки
export NODE_ENV=development

if [ -n "$MOVIES_API_URL" ]; then
    echo "🔧 Movies API: $MOVIES_API_URL"
    export NEXT_PUBLIC_MOVIES_API_URL="$MOVIES_API_URL"
fi

if [ -n "$OSCARS_API_URL" ]; then
    echo "🔧 Oscars API: $OSCARS_API_URL"
    export NEXT_PUBLIC_OSCARS_API_URL="$OSCARS_API_URL"
fi

# Если URL не указаны, используем значения по умолчанию
if [ -z "$MOVIES_API_URL" ]; then
    echo "🔧 Movies API: http://localhost:8081 (по умолчанию)"
fi

if [ -z "$OSCARS_API_URL" ]; then
    echo "🔧 Oscars API: http://localhost:8080 (по умолчанию)"
fi

echo "📦 Запускаем сервер разработки..."
npm run dev

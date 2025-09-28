#!/bin/bash

# Рабочий скрипт для деплоя статики на сервер
# Использование: ./deploy-working.sh [сервер_алиас] [movies_api_url] [oscars_api_url]

# Получаем алиас сервера из аргумента или запрашиваем
if [ -z "$1" ]; then
    echo "📝 Введите алиас сервера (например: helios):"
    read SERVER_ALIAS
else
    SERVER_ALIAS="$1"
fi

if [ -z "$SERVER_ALIAS" ]; then
    echo "❌ Ошибка: не указан алиас сервера"
    echo "Использование: ./deploy-working.sh [сервер_алиас] [movies_api_url] [oscars_api_url]"
    exit 1
fi

# Получаем URL API (опционально)
MOVIES_API_URL="$2"
OSCARS_API_URL="$3"

echo "🔧 Конфигурация API:"
if [ -n "$MOVIES_API_URL" ]; then
    echo "   Movies API: $MOVIES_API_URL"
    export NEXT_PUBLIC_MOVIES_API_URL="$MOVIES_API_URL"
fi
if [ -n "$OSCARS_API_URL" ]; then
    echo "   Oscars API: $OSCARS_API_URL"
    export NEXT_PUBLIC_OSCARS_API_URL="$OSCARS_API_URL"
fi

echo "🚀 Начинаем деплой статики на сервер: $SERVER_ALIAS"

# Проверяем, что мы в правильной директории
if [ ! -f "package.json" ]; then
    echo "❌ Ошибка: package.json не найден. Запустите скрипт из корня проекта."
    exit 1
fi

# Очищаем предыдущую сборку
echo "🧹 Очищаем предыдущую сборку..."
rm -rf out

# Генерируем статику с правильными настройками
echo "📦 Генерируем статику с правильными путями..."
NODE_ENV=production npm run build

# Проверяем, что сборка прошла успешно
if [ ! -d "out" ]; then
    echo "❌ Ошибка: папка out не создана. Проверьте настройки Next.js."
    exit 1
fi

# Показываем структуру файлов
echo "📁 Структура файлов для деплоя:"
echo "   - index.html (с правильными путями /~s367268/soa/)"
echo "   - _next/static/chunks/ (JS файлы)"
echo "   - _next/static/css/ (CSS файлы)"
echo "   - _next/static/media/ (шрифты и изображения)"
echo "   - favicon.ico"

# Копируем файлы на сервер
echo "📁 Копируем файлы на сервер $SERVER_ALIAS..."
rsync -av --delete out/ "$SERVER_ALIAS:~/public_html/soa/"

# Проверяем результат
if [ $? -eq 0 ]; then
    echo "✅ Деплой завершен успешно!"
    echo "🌐 Ваш сайт доступен по адресу: https://se.ifmo.ru/~s367268/soa/"
    echo ""
    echo "📋 Проверьте структуру файлов на сервере:"
    echo "   ssh $SERVER_ALIAS 'ls -la ~/public_html/soa/_next/static/'"
    echo ""
    echo "🔧 Если файлы не загружаются, проверьте:"
    echo "   1. Права доступа к файлам на сервере"
    echo "   2. Настройки веб-сервера (Apache/Nginx)"
    echo "   3. Логи веб-сервера для ошибок 404"
    echo ""
    echo "🎯 Теперь все пути должны быть правильными:"
    echo "   - JS: /~s367268/soa/_next/static/chunks/"
    echo "   - CSS: /~s367268/soa/_next/static/css/"
    echo "   - Шрифты: /~s367268/soa/_next/static/media/"
else
    echo "❌ Ошибка при копировании файлов на сервер"
    exit 1
fi

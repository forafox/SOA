#!/bin/bash

# Финальный скрипт для деплоя статики на сервер
# Использование: ./deploy-final.sh [сервер_алиас]

# Получаем алиас сервера из аргумента или запрашиваем
if [ -z "$1" ]; then
    echo "📝 Введите алиас сервера (например: user@server.com):"
    read SERVER_ALIAS
else
    SERVER_ALIAS="$1"
fi

if [ -z "$SERVER_ALIAS" ]; then
    echo "❌ Ошибка: не указан алиас сервера"
    echo "Использование: ./deploy-final.sh [сервер_алиас]"
    exit 1
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

# Генерируем статику
echo "📦 Генерируем статику..."
npm run build

# Проверяем, что сборка прошла успешно
if [ ! -d "out" ]; then
    echo "❌ Ошибка: папка out не создана. Проверьте настройки Next.js."
    exit 1
fi

# Показываем структуру файлов
echo "📁 Структура файлов для деплоя:"
echo "   - index.html"
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
    echo "🌐 Ваш сайт доступен по адресу: ~/public_html/soa на сервере $SERVER_ALIAS"
    echo ""
    echo "📋 Проверьте структуру файлов на сервере:"
    echo "   ssh $SERVER_ALIAS 'ls -la ~/public_html/soa/'"
    echo "   ssh $SERVER_ALIAS 'ls -la ~/public_html/soa/_next/static/'"
    echo ""
    echo "🔧 Если файлы не загружаются, проверьте:"
    echo "   1. Права доступа к файлам на сервере"
    echo "   2. Настройки веб-сервера (Apache/Nginx)"
    echo "   3. Логи веб-сервера для ошибок 404"
else
    echo "❌ Ошибка при копировании файлов на сервер"
    exit 1
fi

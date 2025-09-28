#!/bin/bash

# Исправленный скрипт для деплоя статики на сервер
# Использование: ./deploy-fixed.sh [сервер_алиас]

# Получаем алиас сервера из аргумента или запрашиваем
if [ -z "$1" ]; then
    echo "📝 Введите алиас сервера (например: user@server.com):"
    read SERVER_ALIAS
else
    SERVER_ALIAS="$1"
fi

if [ -z "$SERVER_ALIAS" ]; then
    echo "❌ Ошибка: не указан алиас сервера"
    echo "Использование: ./deploy-fixed.sh [сервер_алиас]"
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

# Генерируем статику с правильными настройками
echo "📦 Генерируем статику..."
NODE_ENV=production npm run build

# Проверяем, что сборка прошла успешно
if [ ! -d "out" ]; then
    echo "❌ Ошибка: папка out не создана. Проверьте настройки Next.js."
    exit 1
fi

# Создаем временную папку для правильной структуры
echo "📁 Подготавливаем файлы для деплоя..."
TEMP_DIR=$(mktemp -d)
cp -r out/* "$TEMP_DIR/"

# Копируем .htaccess файл
if [ -f "public/.htaccess" ]; then
    cp public/.htaccess "$TEMP_DIR/"
    echo "✅ Скопирован .htaccess файл"
fi

# Копируем файлы на сервер
echo "📁 Копируем файлы на сервер $SERVER_ALIAS..."
rsync -av --delete "$TEMP_DIR/" "$SERVER_ALIAS:~/public_html/soa/"

# Очищаем временную папку
rm -rf "$TEMP_DIR"

# Проверяем результат
if [ $? -eq 0 ]; then
    echo "✅ Деплой завершен успешно!"
    echo "🌐 Ваш сайт доступен по адресу: ~/public_html/soa на сервере $SERVER_ALIAS"
    echo "🔧 Убедитесь, что на сервере включен mod_rewrite для Apache"
else
    echo "❌ Ошибка при копировании файлов на сервер"
    exit 1
fi

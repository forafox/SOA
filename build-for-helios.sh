#!/bin/bash

# Скрипт для полной сборки проекта для деплоя на Гелиос (WildFly)

echo "🚀 Начинаем полную сборку проекта для Гелиос..."

# Проверяем, что мы в корневой директории проекта
if [ ! -f "build-frontend.sh" ]; then
    echo "❌ Ошибка: Запустите скрипт из корневой директории проекта"
    exit 1
fi

# 1. Собираем фронтенд и интегрируем с бэкендом
echo "📦 Шаг 1: Сборка фронтенда..."
./build-frontend.sh

if [ $? -ne 0 ]; then
    echo "❌ Ошибка при сборке фронтенда"
    exit 1
fi

# 2. Собираем WAR файл
echo "🔨 Шаг 2: Сборка WAR файла..."
cd backend-oscars
./gradlew clean war

if [ $? -ne 0 ]; then
    echo "❌ Ошибка при сборке WAR файла"
    exit 1
fi

# 3. Проверяем результат
WAR_FILE="build/libs/backend-oscars-0.0.1-SNAPSHOT.war"
if [ -f "$WAR_FILE" ]; then
    echo "✅ WAR файл успешно создан: $WAR_FILE"
    echo "📊 Размер файла: $(du -h "$WAR_FILE" | cut -f1)"
    echo ""
    echo "🎯 Готово к деплою на Гелиос!"
    echo "📁 Загрузите файл: $(pwd)/$WAR_FILE"
    echo "🌐 После деплоя приложение будет доступно на порту 8080"
else
    echo "❌ WAR файл не найден"
    exit 1
fi

cd ..
echo ""
echo "📋 Следующие шаги:"
echo "1. Загрузите WAR файл на Гелиос"
echo "2. Разверните его в WildFly (настраивается администратором)"
echo "3. Проверьте доступность по адресу: http://your-domain:8080/"

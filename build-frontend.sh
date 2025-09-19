#!/bin/bash

# Скрипт для сборки фронтенда и интеграции с Spring Boot

echo "🚀 Начинаем сборку фронтенда..."

# Переходим в директорию фронтенда
cd frontend

# Устанавливаем зависимости если нужно
if [ ! -d "node_modules" ]; then
    echo "📦 Устанавливаем зависимости..."
    npm install
fi

# Собираем фронтенд для статического экспорта
echo "🔨 Собираем фронтенд..."
npm run build

# Проверяем, что сборка прошла успешно
if [ ! -d "out" ]; then
    echo "❌ Ошибка: директория out не найдена. Сборка не удалась."
    exit 1
fi

# Очищаем старую статику в Spring Boot
echo "🧹 Очищаем старую статику..."
rm -rf ../backend-oscars/src/main/resources/static/*

# Копируем собранный фронтенд в Spring Boot
echo "📁 Копируем статику в Spring Boot..."
cp -r out/* ../backend-oscars/src/main/resources/static/

echo "✅ Фронтенд успешно интегрирован с Spring Boot!"
echo "🌐 Теперь можно запустить Spring Boot и фронтенд будет доступен на том же порту"

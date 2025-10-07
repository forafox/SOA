FROM node:20

WORKDIR /app

# Копируем package.json и package-lock.json
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install --legacy-peer-deps

# Копируем исходники
COPY . ./

# Сборка проекта
RUN npm run build

EXPOSE 3000

# Запуск серверного рендеринга
CMD ["npm", "run", "start"]

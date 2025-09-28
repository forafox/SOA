// Конфигурация для разных окружений
export interface BackendConfig {
  moviesApiUrl: string
  oscarsApiUrl: string
  environment: 'development' | 'production'
}

// Конфигурации для разных окружений
const configs: Record<string, BackendConfig> = {
  // Локальная разработка
  development: {
    moviesApiUrl: 'http://localhost:8081',
    oscarsApiUrl: 'http://localhost:8080',
    environment: 'development'
  },
  
  // Продакшн на сервере
  production: {
    moviesApiUrl: 'https://se.ifmo.ru/~s367268/movies-api',
    oscarsApiUrl: 'https://se.ifmo.ru/~s367268/oscars-api',
    environment: 'production'
  },
  
  // Кастомная конфигурация (можно переопределить через переменные окружения)
  custom: {
    moviesApiUrl: process.env.NEXT_PUBLIC_MOVIES_API_URL || 'http://localhost:8081',
    oscarsApiUrl: process.env.NEXT_PUBLIC_OSCARS_API_URL || 'http://localhost:8080',
    environment: (process.env.NODE_ENV as 'development' | 'production') || 'development'
  }
}

// Функция для получения конфигурации
export function getBackendConfig(): BackendConfig {
  // Проверяем переменные окружения
  if (process.env.NEXT_PUBLIC_MOVIES_API_URL || process.env.NEXT_PUBLIC_OSCARS_API_URL) {
    return configs.custom
  }
  
  // Определяем окружение
  const isProduction = process.env.NODE_ENV === 'production'
  
  return isProduction ? configs.production : configs.development
}

// Функция для получения URL callback'ов
export function getCallbackUrls(): { onAwarded: string; notifyAdmins: string; notifyOscarsTeam: string } {
  const config = getBackendConfig()
  
  // В продакшене используем полные URL, в разработке - относительные
  if (config.environment === 'production') {
    const baseUrl = 'https://se.ifmo.ru/~s367268/soa'
    return {
      onAwarded: `${baseUrl}/api/callbacks/on-awarded`,
      notifyAdmins: `${baseUrl}/api/callbacks/notify-admins`,
      notifyOscarsTeam: `${baseUrl}/api/callbacks/notify-oscars-team`
    }
  } else {
    const baseUrl = typeof window !== 'undefined' ? window.location.origin : 'http://localhost:3000'
    return {
      onAwarded: `${baseUrl}/api/callbacks/on-awarded`,
      notifyAdmins: `${baseUrl}/api/callbacks/notify-admins`,
      notifyOscarsTeam: `${baseUrl}/api/callbacks/notify-oscars-team`
    }
  }
}

// Экспортируем текущую конфигурацию
export const backendConfig = getBackendConfig()
export const callbackUrls = getCallbackUrls()

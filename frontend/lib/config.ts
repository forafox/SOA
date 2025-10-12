// Конфигурация для разных окружений
export interface BackendConfig {
  moviesApiUrl: string
  oscarsApiUrl: string
  environment: 'development' | 'production'
}

// Конфигурация для разных окружений
export interface BackendConfig {
  moviesApiUrl: string
  oscarsApiUrl: string
  environment: 'development' | 'production'
}

// Функция для получения конфигурации во время выполнения
export function getBackendConfig(): BackendConfig {
  // Проверяем переменные окружения во время сборки
  const moviesApiUrl = process.env.NEXT_PUBLIC_MOVIES_API_URL
  const oscarsApiUrl = process.env.NEXT_PUBLIC_OSCARS_API_URL
  
  // Отладочная информация
  console.log('🔧 Config Debug:', {
    moviesApiUrl,
    oscarsApiUrl,
    NODE_ENV: process.env.NODE_ENV,
    NEXT_PUBLIC_MOVIES_API_URL: process.env.NEXT_PUBLIC_MOVIES_API_URL,
    NEXT_PUBLIC_OSCARS_API_URL: process.env.NEXT_PUBLIC_OSCARS_API_URL
  })
  
  // Если переменные окружения заданы, используем их
  if (moviesApiUrl && oscarsApiUrl) {
    console.log('✅ Using environment variables:', { moviesApiUrl, oscarsApiUrl })
    return {
      moviesApiUrl,
      oscarsApiUrl,
      environment: (process.env.NODE_ENV as 'development' | 'production') || 'development'
    }
  }
  
  // Иначе определяем по NODE_ENV
  const isProduction = process.env.NODE_ENV === 'production'
  
  if (isProduction) {
    console.log('🌐 Using production config')
    return {
      moviesApiUrl: 'https://se.ifmo.ru/~s367268/movies-api',
      oscarsApiUrl: 'https://se.ifmo.ru/~s367268/oscars-api',
      environment: 'production'
    }
  } else {
    console.log('🐳 Using development config')
    return {
      moviesApiUrl: 'http://localhost:8080',
      oscarsApiUrl: 'http://localhost:8081',
      environment: 'development'
    }
  }
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

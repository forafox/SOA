// Конфигурация для встроенного режима (embedded mode)
// Используется когда фронтенд встроен в Spring приложение

// Определяем базовый URL в зависимости от режима работы
const getBaseUrl = (): string => {
  // В встроенном режиме API доступно по тому же адресу
  if (typeof window !== "undefined") {
    // Включаем context path для WildFly деплоймента
    const origin = window.location.origin;
    const pathname = window.location.pathname;
    
    // Если мы в WildFly (есть context path), включаем его в base URL
    if (pathname.startsWith('/backend-oscars')) {
      const contextPath = pathname.split('/').slice(0, 2).join('/'); // /backend-oscars-X.X.X-SNAPSHOT
      return origin + contextPath;
    }
    
    return origin;
  }
  return "";
};

// Получаем правильную конфигурацию API URLs
const getMoviesApiUrl = (): string => {
  if (process.env.NEXT_PUBLIC_MOVIES_API_URL) {
    return process.env.NEXT_PUBLIC_MOVIES_API_URL;
  }
  
  if (typeof window !== "undefined") {
    // В браузере определяем по текущему location
    if (window.location.pathname.startsWith('/backend-oscars')) {
      return window.location.origin + '/backend-oscars-0.0.1-SNAPSHOT/api';
    }
  }
  
  return getBaseUrl() + '/api';
};

// Экспортируем конфигурацию для использования в API клиенте
export const backendConfig = {
  // Movies API теперь проксируется через этот же сервис
  get moviesApiUrl() {
    return getMoviesApiUrl();
  },
  
  // Oscars API обслуживается этим же приложением
  get oscarsApiUrl() {
    return getBaseUrl();
  },
  
  environment: process.env.NEXT_PUBLIC_EMBEDDED_MODE ? 'embedded' : 'development'
};

// Callback URLs для уведомлений
export const callbackUrls = {
  // В встроенном режиме callback URL должен указывать на текущий сервер
  base: getBaseUrl(),
  
  // Specific callback endpoints
  notifyAdmins: `${getBaseUrl()}/api/callbacks/notify-admins`,
  notifyOscarsTeam: `${getBaseUrl()}/api/callbacks/notify-oscars-team`, 
  onAwarded: `${getBaseUrl()}/api/callbacks/on-awarded`
};

// Функция для получения полного URL callback'а
export const getCallbackUrl = (type: 'notifyAdmins' | 'notifyOscarsTeam' | 'onAwarded'): string => {
  return callbackUrls[type];
};

// Логирование конфигурации для отладки (выполняется после инициализации DOM)
if (typeof window !== "undefined") {
  // Отложенное логирование для гарантии что конфигурация инициализирована
  setTimeout(() => {
    console.log('🔧 Frontend config initialized:', {
      moviesApiUrl: backendConfig.moviesApiUrl,
      oscarsApiUrl: backendConfig.oscarsApiUrl,
      environment: backendConfig.environment,
      baseUrl: getBaseUrl(),
      currentLocation: {
        origin: window.location.origin,
        pathname: window.location.pathname,
        href: window.location.href
      }
    });
  }, 100);
}

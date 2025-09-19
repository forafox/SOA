// Общее хранилище коллбэков для всех API routes
export interface CallbackData {
  id: string
  type: string
  data: unknown
  status: string
  timestamp: string
}

// Глобальный синглтон, чтобы переживать hot-reload и отдельные импорт-контексты
// Используем символ на globalThis, чтобы не конфликтовать по именам
const GLOBAL_CALLBACKS_KEY = '__recent_callbacks_storage__'

// Инициализируем единый разделяемый массив в globalThis
const sharedStore: CallbackData[] = (globalThis as any)[GLOBAL_CALLBACKS_KEY] ?? []
if (!(GLOBAL_CALLBACKS_KEY in (globalThis as any))) {
  ;(globalThis as any)[GLOBAL_CALLBACKS_KEY] = sharedStore
}

// Ссылка на общий массив
const recentCallbacks: CallbackData[] = sharedStore

export function addCallback(callback: Omit<CallbackData, 'id' | 'timestamp'>) {
  const newCallback: CallbackData = {
    ...callback,
    id: Math.random().toString(36).substr(2, 9),
    timestamp: new Date().toISOString()
  }
  
  console.log('📦 CallbackStorage: Adding callback:', newCallback)
  
  // Добавляем в начало списка
  recentCallbacks.unshift(newCallback)
  
  // Ограничиваем до 10 последних
  if (recentCallbacks.length > 10) {
    recentCallbacks.splice(10)
  }
  
  console.log('📦 CallbackStorage: Total callbacks in storage:', recentCallbacks.length)
  
  return newCallback
}

export function getRecentCallbacks(): CallbackData[] {
  console.log('📦 CallbackStorage: Returning callbacks:', recentCallbacks.length)
  return [...recentCallbacks]
}

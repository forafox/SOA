// Общее хранилище коллбэков для всех API routes
interface CallbackData {
  id: string
  type: string
  data: unknown
  status: string
  timestamp: string
}

let recentCallbacks: CallbackData[] = []

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
  recentCallbacks = recentCallbacks.slice(0, 10)
  
  console.log('📦 CallbackStorage: Total callbacks in storage:', recentCallbacks.length)
  
  return newCallback
}

export function getRecentCallbacks(): CallbackData[] {
  console.log('📦 CallbackStorage: Returning callbacks:', recentCallbacks.length)
  return [...recentCallbacks]
}

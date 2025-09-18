"use client"

import { useEffect, useRef, useState } from "react"
import { useToast } from "@/hooks/use-toast"

export function SmartCallbackMonitor() {
  const { toast } = useToast()
  const [isMonitoring, setIsMonitoring] = useState(false)
  const lastCallbackId = useRef<string | null>(null)
  const shownCallbacks = useRef<Set<string>>(new Set())
  const monitoringTimeout = useRef<NodeJS.Timeout | null>(null)
  const pollingInterval = useRef<NodeJS.Timeout | null>(null)

  useEffect(() => {
    console.log('🧠 SmartCallbackMonitor: Component mounted')
    
    // Слушаем события о начале операций, которые должны вызвать коллбэки
    const handleOperationStart = (event: CustomEvent) => {
      const { operationId } = event.detail
      console.log('🧠 SmartCallbackMonitor: Operation started:', operationId)
      
      // Начинаем мониторинг
      startMonitoring()
    }

    // Слушаем события о получении коллбэков
    const handleCallbackReceived = (event: CustomEvent) => {
      const { type } = event.detail
      console.log('🧠 SmartCallbackMonitor: Callback received via event:', type)
      
      // Останавливаем мониторинг, так как коллбэк уже получен
      stopMonitoring()
    }

    window.addEventListener('operation-start', handleOperationStart as EventListener)
    window.addEventListener('callback-received', handleCallbackReceived as EventListener)
    
    return () => {
      window.removeEventListener('operation-start', handleOperationStart as EventListener)
      window.removeEventListener('callback-received', handleCallbackReceived as EventListener)
      stopMonitoring()
    }
  }, [])

  const startMonitoring = () => {
    if (isMonitoring) {
      console.log('🧠 SmartCallbackMonitor: Already monitoring, skipping')
      return
    }

    console.log('🧠 SmartCallbackMonitor: Starting smart monitoring...')
    
    // Останавливаем мониторинг через 30 секунд максимум
    monitoringTimeout.current = setTimeout(() => {
      console.log('🧠 SmartCallbackMonitor: Monitoring timeout reached, stopping')
      stopMonitoring()
    }, 30000)
    
    // Начинаем проверку каждые 2 секунды
    pollingInterval.current = setInterval(() => {
      checkCallbacks()
    }, 2000)
    
    // Устанавливаем состояние после настройки интервалов
    setIsMonitoring(true)
    
    // Первая проверка сразу
    checkCallbacks()
  }

  const stopMonitoring = () => {
    console.log('🧠 SmartCallbackMonitor: Stopping monitoring...')
    setIsMonitoring(false)
    
    if (monitoringTimeout.current) {
      clearTimeout(monitoringTimeout.current)
      monitoringTimeout.current = null
    }
    
    if (pollingInterval.current) {
      clearInterval(pollingInterval.current)
      pollingInterval.current = null
    }
  }

  const checkCallbacks = async () => {
    // Проверяем, что у нас есть активный интервал (значит мониторинг запущен)
    if (!pollingInterval.current) {
      console.log('🧠 SmartCallbackMonitor: No active polling interval, skipping check')
      return
    }

    try {
      console.log('🧠 SmartCallbackMonitor: Checking for new callbacks...')
      const response = await fetch('/api/callbacks/recent')
      const data = await response.json()
      
      console.log('🧠 SmartCallbackMonitor: Response data:', data)
      
      if (data.callbacks && data.callbacks.length > 0) {
        console.log('🧠 SmartCallbackMonitor: Found callbacks:', data.callbacks.length)
        console.log('🧠 SmartCallbackMonitor: Last callback ID:', lastCallbackId.current)
        
        // Проверяем, есть ли новые коллбэки
        const newCallbacks = data.callbacks.filter((cb: any) => 
          !lastCallbackId.current || cb.id !== lastCallbackId.current
        )
        
        if (newCallbacks.length > 0) {
          console.log('🧠 SmartCallbackMonitor: New callbacks found:', newCallbacks.length)
          
          newCallbacks.forEach((callback: any) => {
            console.log('🧠 SmartCallbackMonitor: Processing callback:', callback)
            
            // Проверяем, не показывали ли мы уже этот коллбэк
            if (shownCallbacks.current.has(callback.id)) {
              console.log('🧠 SmartCallbackMonitor: Callback already shown, skipping:', callback.id)
              return
            }
            
            const { type, data, status = 'success' } = callback
            
            // Показываем toast уведомление в зависимости от типа коллбэка
            switch (type) {
              case 'onAwarded':
                toast({
                  title: "🎬 Фильмы награждены!",
                  description: `Получено уведомление о награждении фильмов по длине. Обработано ${data?.updatedMovies?.length || 0} фильмов.`,
                  duration: 8000,
                  className: "border-green-500 bg-green-50 text-green-900",
                })
                break
                
              case 'notifyAdmins':
                toast({
                  title: "👥 Уведомление администраторам",
                  description: `Получено уведомление для администраторов о награждении ${data?.updatedMovies?.length || 0} фильмов с малым количеством Оскаров.`,
                  duration: 8000,
                  className: "border-blue-500 bg-blue-50 text-blue-900",
                })
                break
                
              case 'notifyOscarsTeam':
                toast({
                  title: "🏆 Команда Оскаров уведомлена",
                  description: `Получено уведомление для команды Оскаров о добавлении ${data?.addedOscars || 0} Оскаров к фильму ID ${data?.movieId}.`,
                  duration: 8000,
                  className: "border-purple-500 bg-purple-50 text-purple-900",
                })
                break
                
              default:
                toast({
                  title: "🏆 Коллбэк получен!",
                  description: `Получено уведомление типа: ${type}`,
                  duration: 8000,
                  className: "border-purple-500 bg-purple-50 text-purple-900",
                })
            }
            
            // Отмечаем коллбэк как показанный
            shownCallbacks.current.add(callback.id)
            console.log('🧠 SmartCallbackMonitor: Callback marked as shown:', callback.id)
          })
          
          // Обновляем ID последнего коллбэка
          lastCallbackId.current = data.callbacks[0].id
          
          // Останавливаем мониторинг, так как получили коллбэк
          stopMonitoring()
        }
      }
    } catch (error) {
      console.error('🧠 SmartCallbackMonitor: Error checking callbacks:', error)
    }
  }

  // Этот компонент не рендерит ничего видимого
  return null
}

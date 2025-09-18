"use client"

import { useEffect } from "react"
import { useToast } from "@/hooks/use-toast"

export function GlobalCallbackNotifications() {
  const { toast } = useToast()

  useEffect(() => {
    console.log('🌍 GlobalCallbackNotifications: Component mounted, setting up listeners')
    
    // Слушаем глобальные события коллбэков
    const handleCallbackReceived = (event: CustomEvent) => {
      const eventData = event.detail
      console.log('🌍 GlobalCallbackNotifications: Received callback event:', eventData)
      console.log('🌍 GlobalCallbackNotifications: Event type:', event.type)
      console.log('🌍 GlobalCallbackNotifications: Event detail:', event.detail)
      
      const { type, data } = eventData
      
      // Показываем toast уведомление в зависимости от типа коллбэка
      console.log('🌍 GlobalCallbackNotifications: Processing callback type:', type)
      
      switch (type) {
        case 'onAwarded':
          console.log('🌍 GlobalCallbackNotifications: Showing onAwarded toast')
          toast({
            title: "🎬 Фильмы награждены!",
            description: `Получено уведомление о награждении фильмов по длине. Обработано ${data?.updatedMovies?.length || 0} фильмов.`,
            duration: 8000,
            className: "border-green-500 bg-green-50 text-green-900",
          })
          break
          
        case 'notifyAdmins':
          console.log('🌍 GlobalCallbackNotifications: Showing notifyAdmins toast')
          toast({
            title: "👥 Уведомление администраторам",
            description: `Получено уведомление для администраторов о награждении ${data?.updatedMovies?.length || 0} фильмов с малым количеством Оскаров.`,
            duration: 8000,
            className: "border-blue-500 bg-blue-50 text-blue-900",
          })
          break
          
        case 'notifyOscarsTeam':
          console.log('🌍 GlobalCallbackNotifications: Showing notifyOscarsTeam toast')
          toast({
            title: "🏆 Команда Оскаров уведомлена",
            description: `Получено уведомление для команды Оскаров о добавлении ${data?.addedOscars || 0} Оскаров к фильму ID ${data?.movieId}.`,
            duration: 8000,
            className: "border-purple-500 bg-purple-50 text-purple-900",
          })
          break
          
        default:
          console.log('🌍 GlobalCallbackNotifications: Showing default toast for type:', type)
          toast({
            title: "🏆 Коллбэк получен!",
            description: `Получено уведомление типа: ${type}`,
            duration: 8000,
            className: "border-purple-500 bg-purple-50 text-purple-900",
          })
      }
    }
    
    // Подписываемся на глобальные события
    window.addEventListener('callback-received', handleCallbackReceived as EventListener)
    
    return () => {
      window.removeEventListener('callback-received', handleCallbackReceived as EventListener)
    }
  }, [toast])

  // Этот компонент не рендерит ничего видимого
  return null
}

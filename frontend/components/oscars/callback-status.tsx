"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Bell, CheckCircle, XCircle, Clock } from "lucide-react"
import { useToast } from "@/hooks/use-toast"

interface CallbackStatusProps {
  className?: string
}

interface CallbackEvent {
  id: string
  type: 'onAwarded' | 'notifyAdmins' | 'notifyOscarsTeam'
  status: 'pending' | 'success' | 'error'
  timestamp: Date
  message: string
  data?: unknown
}

export function CallbackStatus({ className }: CallbackStatusProps) {
  const [callbacks, setCallbacks] = useState<CallbackEvent[]>([])
  const [isVisible, setIsVisible] = useState(true)
  const [newCallbackCount, setNewCallbackCount] = useState(0)
  const { toast } = useToast()

  useEffect(() => {
    // Слушаем события коллбэков через window events
    const handleCallbackReceived = (event: CustomEvent) => {
      const eventData = event.detail
      console.log('🔔 CallbackStatus: Received callback event:', eventData)
      
      const { type, data, status = 'success' } = eventData
      
      const callbackEvent: CallbackEvent = {
        id: Math.random().toString(36).substr(2, 9),
        type,
        status,
        timestamp: new Date(),
        message: getCallbackMessage(type, data),
        data
      }
      
      console.log('🔔 CallbackStatus: Created callback event:', callbackEvent)
      setCallbacks(prev => [callbackEvent, ...prev].slice(0, 10))
      setNewCallbackCount(prev => prev + 1)
      setIsVisible(true)
      
      // Toast уведомления теперь показываются глобально через GlobalCallbackNotifications
    }
    
    // Подписываемся на window events
    window.addEventListener('callback-received', handleCallbackReceived as EventListener)
    
    return () => {
      window.removeEventListener('callback-received', handleCallbackReceived as EventListener)
    }
  }, [toast])

  const getCallbackMessage = (type: string, data: unknown): string => {
    switch (type) {
      case 'onAwarded':
        return `Награждение по длине: обработано ${(data as { updatedMovies?: unknown[] })?.updatedMovies?.length || 0} фильмов`
      case 'notifyAdmins':
        return `Уведомление администраторам: ${(data as { updatedMovies?: unknown[] })?.updatedMovies?.length || 0} фильмов с малым количеством Оскаров`
      case 'notifyOscarsTeam':
        return `Команда Оскаров: добавлено ${(data as { addedOscars?: number; movieId?: number })?.addedOscars || 0} Оскаров к фильму ID ${(data as { addedOscars?: number; movieId?: number })?.movieId}`
      default:
        return 'Получен коллбэк'
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'success':
        return <CheckCircle className="h-4 w-4 text-green-500" />
      case 'error':
        return <XCircle className="h-4 w-4 text-red-500" />
      case 'pending':
        return <Clock className="h-4 w-4 text-yellow-500" />
      default:
        return <Bell className="h-4 w-4 text-gray-500" />
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'success':
        return <Badge variant="default" className="bg-green-500">Успешно</Badge>
      case 'error':
        return <Badge variant="destructive">Ошибка</Badge>
      case 'pending':
        return <Badge variant="secondary">Ожидание</Badge>
      default:
        return <Badge variant="outline">Неизвестно</Badge>
    }
  }

  if (callbacks.length === 0) {
    return null
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <Bell className="h-5 w-5" />
            Статус коллбэков
            {newCallbackCount > 0 && (
              <Badge variant="destructive" className="animate-pulse">
                {newCallbackCount} новых
              </Badge>
            )}
          </CardTitle>
          <button
            onClick={() => {
              setIsVisible(!isVisible)
              if (isVisible) {
                setNewCallbackCount(0) // Сбрасываем счетчик при скрытии
              }
            }}
            className="text-sm text-muted-foreground hover:text-foreground"
          >
            {isVisible ? 'Скрыть' : 'Показать'} ({callbacks.length})
          </button>
        </div>
      </CardHeader>
      {isVisible && (
        <CardContent>
          <div className="space-y-3 max-h-64 overflow-y-auto">
            {callbacks.map((callback) => (
              <div key={callback.id} className="flex items-start gap-3 p-3 border rounded-lg">
                {getStatusIcon(callback.status)}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    {getStatusBadge(callback.status)}
                    <span className="text-xs text-muted-foreground">
                      {callback.timestamp.toLocaleTimeString()}
                    </span>
                  </div>
                  <p className="text-sm">{callback.message}</p>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      )}
    </Card>
  )
}

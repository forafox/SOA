"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { TestTube, Send } from "lucide-react"
import { useToast } from "@/hooks/use-toast"

export function CallbackTester() {
  const [loading, setLoading] = useState(false)
  const { toast } = useToast()

  const testCallback = async (type: 'onAwarded' | 'notifyAdmins' | 'notifyOscarsTeam') => {
    setLoading(true)
    
    // Отправляем событие о начале операции
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('operation-start', {
        detail: { operationId: type }
      }))
    }
    
    try {
      const testData = {
        onAwarded: {
          updatedMovies: [
            { id: 1, name: "Test Movie 1", oscarsCount: 3 },
            { id: 2, name: "Test Movie 2", oscarsCount: 2 }
          ]
        },
        notifyAdmins: {
          updatedMovies: [
            { id: 3, name: "Low Oscar Movie", oscarsCount: 1 }
          ]
        },
        notifyOscarsTeam: {
          movieId: 1,
          addedOscars: 2,
          category: "Best Picture",
          date: new Date().toISOString()
        }
      }

      const response = await fetch('/api/callbacks/test', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          type,
          data: testData[type]
        })
      })

      if (response.ok) {
        const result = await response.json()
        
        toast({
          title: "Тест запущен",
          description: `Коллбэк ${type} обработан успешно`,
          duration: 3000,
        })
        
        // Если есть данные коллбэка в ответе, отправляем событие в UI
        if (result.callback) {
          console.log('🧪 Test: Sending callback event to UI:', result.callback)
          
          // Отправляем событие через window events
          if (typeof window !== 'undefined') {
            window.dispatchEvent(new CustomEvent('callback-received', {
              detail: result.callback
            }))
          }
        }
      } else {
        throw new Error('Failed to send test callback')
      }
    } catch (error) {
      toast({
        title: "Ошибка теста",
        description: "Не удалось отправить тестовый коллбэк",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  const callbackTypes = [
    { type: 'onAwarded' as const, label: 'Награждение по длине', description: 'Тестирует коллбэк для награждения фильмов по длине' },
    { type: 'notifyAdmins' as const, label: 'Уведомление администраторов', description: 'Тестирует коллбэк для уведомления администраторов' },
    { type: 'notifyOscarsTeam' as const, label: 'Команда Оскаров', description: 'Тестирует коллбэк для команды Оскаров' }
  ]

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <TestTube className="h-5 w-5" />
          Тестирование коллбэков
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <p className="text-sm text-muted-foreground">
            Используйте эти кнопки для тестирования различных типов коллбэков. 
            Коллбэки будут обработаны и отображены в статусе коллбэков ниже.
          </p>
          
          <div className="grid gap-3">
            {callbackTypes.map(({ type, label, description }) => (
              <div key={type} className="flex items-center justify-between p-3 border rounded-lg">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <Badge variant="outline">{type}</Badge>
                    <span className="font-medium">{label}</span>
                  </div>
                  <p className="text-sm text-muted-foreground">{description}</p>
                </div>
                <Button
                  onClick={() => testCallback(type)}
                  disabled={loading}
                  size="sm"
                  variant="outline"
                >
                  {loading ? (
                    "Отправка..."
                  ) : (
                    <>
                      <Send className="h-4 w-4 mr-2" />
                      Тест
                    </>
                  )}
                </Button>
              </div>
            ))}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

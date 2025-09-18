"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Clock } from "lucide-react"

interface CallbackPendingProps {
  className?: string
}

export function CallbackPending({ className }: CallbackPendingProps) {
  const [pendingCallbacks, setPendingCallbacks] = useState<Set<string>>(new Set())
  const [, setCompletedCallbacks] = useState<Set<string>>(new Set())

  useEffect(() => {
    // Отслеживаем начало операции
    const handleOperationStart = (event: CustomEvent) => {
      const { operationId } = event.detail
      console.log("⏳ CallbackPending: Operation started:", operationId)
      setPendingCallbacks((prev) => new Set(prev).add(operationId))
    }

    // Слушаем получение коллбэка
    const handleCallbackReceived = (event: CustomEvent) => {
      const { type } = event.detail
      console.log("⏳ CallbackPending: Callback received:", type)

      setCompletedCallbacks((prev) => new Set(prev).add(type))
      setPendingCallbacks((prev) => {
        const newSet = new Set(prev)
        newSet.delete(type)
        console.log("⏳ CallbackPending: Removed pending callback:", type)
        return newSet
      })
    }

    window.addEventListener("operation-start", handleOperationStart as EventListener)
    window.addEventListener("callback-received", handleCallbackReceived as EventListener)

    return () => {
      window.removeEventListener("operation-start", handleOperationStart as EventListener)
      window.removeEventListener("callback-received", handleCallbackReceived as EventListener)
    }
  }, [])

  if (pendingCallbacks.size === 0) return null

  return (
      <Card className={`${className ?? ""} border-yellow-500 bg-yellow-50`}>
        <CardContent className="pt-6">
          <div className="flex items-center gap-3">
            <Clock className="h-5 w-5 text-yellow-600 animate-spin" />
            <div className="flex-1">
              <div className="font-medium text-yellow-800">Ожидание коллбэков...</div>
              <div className="text-sm text-yellow-700">
                Операция выполнена, ожидаем уведомления от сервера (задержка ~3 сек)
              </div>
            </div>
            <div className="flex gap-2">
              {Array.from(pendingCallbacks).map((callback) => (
                  <Badge
                      key={callback}
                      variant="outline"
                      className="border-yellow-600 text-yellow-700"
                  >
                    {callback}
                  </Badge>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>
  )
}
"use client"

import { useEffect, useRef, useState, useCallback } from "react"
import { useToast } from "@/hooks/use-toast"

export function SmartCallbackMonitor() {
  const { toast } = useToast()
  const [isMonitoring, setIsMonitoring] = useState(false)
  const lastCallbackId = useRef<string | null>(null)
  const shownCallbacks = useRef<Set<string>>(new Set())
  const monitoringTimeout = useRef<NodeJS.Timeout | null>(null)
  const pollingInterval = useRef<NodeJS.Timeout | null>(null)

  const checkCallbacksRef = useRef<() => Promise<void>>()
  const startMonitoringRef = useRef<() => void>()
  const stopMonitoringRef = useRef<() => void>()

  const stopMonitoring = useCallback(() => {
    console.log("🧠 SmartCallbackMonitor: Stopping monitoring...")
    setIsMonitoring(false)

    if (monitoringTimeout.current) {
      clearTimeout(monitoringTimeout.current)
      monitoringTimeout.current = null
    }

    if (pollingInterval.current) {
      clearInterval(pollingInterval.current)
      pollingInterval.current = null
    }
  }, [])

  stopMonitoringRef.current = stopMonitoring

  const checkCallbacks = useCallback(async () => {
    if (!pollingInterval.current) {
      console.log("🧠 SmartCallbackMonitor: No active polling interval, skipping check")
      return
    }

    try {
      console.log("🧠 SmartCallbackMonitor: Checking for new callbacks...")
      const response = await fetch("/api/callbacks/recent")
      const data = await response.json()

      console.log("🧠 SmartCallbackMonitor: Response data:", data)
      console.log("🧠 SmartCallbackMonitor: Response status:", response.status)

      if (data.callbacks && data.callbacks.length > 0) {
        console.log("🧠 SmartCallbackMonitor: Found callbacks:", data.callbacks.length)
        console.log("🧠 SmartCallbackMonitor: Last callback ID:", lastCallbackId.current)
        console.log("🧠 SmartCallbackMonitor: All callbacks:", data.callbacks.map((cb: { id: string; type: string }) => ({ id: cb.id, type: cb.type })))

        const newCallbacks = data.callbacks.filter(
            (cb: { id: string }) => !lastCallbackId.current || cb.id !== lastCallbackId.current
        )

        console.log("🧠 SmartCallbackMonitor: New callbacks count:", newCallbacks.length)

        if (newCallbacks.length > 0) {
          console.log("🧠 SmartCallbackMonitor: New callbacks found:", newCallbacks.length)

          newCallbacks.forEach(
              (callback: { id: string; type: string; data: unknown; status?: string }) => {
                console.log("🧠 SmartCallbackMonitor: Processing callback:", callback)

                if (shownCallbacks.current.has(callback.id)) {
                  console.log("🧠 SmartCallbackMonitor: Callback already shown, skipping:", callback.id)
                  return
                }

                const { type, data } = callback

                switch (type) {
                  case "onAwarded":
                    toast({
                      title: "🎬 Фильмы награждены!",
                      description: `Получено уведомление о награждении фильмов по длине. Обработано ${
                          (data as { updatedMovies?: unknown[] })?.updatedMovies?.length || 0
                      } фильмов.`,
                      duration: 8000,
                      className: "border-green-500 bg-green-50 text-green-900",
                    })
                    break

                  case "notifyAdmins":
                    toast({
                      title: "👥 Уведомление администраторам",
                      description: `Получено уведомление для администраторов о награждении ${
                          (data as { updatedMovies?: unknown[] })?.updatedMovies?.length || 0
                      } фильмов с малым количеством Оскаров.`,
                      duration: 8000,
                      className: "border-blue-500 bg-blue-50 text-blue-900",
                    })
                    break

                  case "notifyOscarsTeam":
                    toast({
                      title: "🏆 Команда Оскаров уведомлена",
                      description: `Получено уведомление для команды Оскаров о добавлении ${
                          (data as { addedOscars?: number; movieId?: number })?.addedOscars || 0
                      } Оскаров к фильму ID ${
                          (data as { addedOscars?: number; movieId?: number })?.movieId
                      }.`,
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

                shownCallbacks.current.add(callback.id)
                console.log("🧠 SmartCallbackMonitor: Callback marked as shown:", callback.id)
              }
          )

          lastCallbackId.current = data.callbacks[0].id

          // Останавливаем мониторинг только если получили новые коллбэки
          console.log("🧠 SmartCallbackMonitor: Stopping monitoring after receiving new callbacks")
          stopMonitoringRef.current?.()
        }
      }
    } catch (error) {
      console.error("🧠 SmartCallbackMonitor: Error checking callbacks:", error)
    }
  }, [toast])

  checkCallbacksRef.current = checkCallbacks

  const startMonitoring = useCallback(() => {
    if (isMonitoring) {
      console.log("🧠 SmartCallbackMonitor: Already monitoring, skipping")
      return
    }

    console.log("🧠 SmartCallbackMonitor: Starting smart monitoring...")

    // Останавливаем мониторинг через 30 секунд максимум
    monitoringTimeout.current = setTimeout(() => {
      console.log("🧠 SmartCallbackMonitor: Monitoring timeout reached, stopping")
      stopMonitoringRef.current?.()
    }, 30000)

    // Начинаем проверку каждые 2 секунды
    pollingInterval.current = setInterval(() => {
      checkCallbacksRef.current?.()
    }, 2000)

    // Устанавливаем состояние после настройки интервалов
    setIsMonitoring(true)

    // Первая проверка через небольшую задержку, чтобы дать серверу время обработать коллбэк
    setTimeout(() => {
      checkCallbacksRef.current?.()
    }, 500)
  }, [isMonitoring])

  startMonitoringRef.current = startMonitoring

  useEffect(() => {
    console.log("🧠 SmartCallbackMonitor: Component mounted")

    const handleOperationStart = (event: CustomEvent) => {
      const { operationId } = event.detail
      console.log("🧠 SmartCallbackMonitor: Operation started:", operationId)
      startMonitoringRef.current?.()
    }

    const handleCallbackReceived = (event: CustomEvent) => {
      const { type } = event.detail
      console.log("🧠 SmartCallbackMonitor: Callback received via event:", type)
      stopMonitoringRef.current?.()
    }

    window.addEventListener("operation-start", handleOperationStart as EventListener)
    window.addEventListener("callback-received", handleCallbackReceived as EventListener)

    return () => {
      console.log("🧠 SmartCallbackMonitor: Component unmounting, cleaning up...")
      window.removeEventListener("operation-start", handleOperationStart as EventListener)
      window.removeEventListener("callback-received", handleCallbackReceived as EventListener)
      stopMonitoringRef.current?.()
    }
  }, [])

  return null
}
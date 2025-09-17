"use client"

import { useState, useEffect } from "react"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Database, Globe } from "lucide-react"

export function MockDataToggle() {
  const [useMockData, setUseMockData] = useState(false)

  useEffect(() => {
    const savedValue = localStorage.getItem("useMockData") === "true"
    setUseMockData(savedValue)
  }, [])

  const handleToggle = (checked: boolean) => {
    setUseMockData(checked)
    localStorage.setItem("useMockData", checked.toString())
    if (typeof window !== "undefined") {
      window.location.reload()
    }
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader className="pb-3">
        <CardTitle className="flex items-center gap-2 text-lg">
          <Database className="h-5 w-5" />
          Источник данных
        </CardTitle>
        <CardDescription>Переключение между настоящим API и демонстрационными данными</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            {useMockData ? (
              <Database className="h-4 w-4 text-blue-500" />
            ) : (
              <Globe className="h-4 w-4 text-green-500" />
            )}
            <Label htmlFor="mock-toggle" className="text-sm font-medium">
              {useMockData ? "Демо данные" : "Реальный API"}
            </Label>
          </div>
          <Switch id="mock-toggle" checked={useMockData} onCheckedChange={handleToggle} />
        </div>

        <div className="text-xs text-muted-foreground space-y-1">
          <p>
            <strong>Демо данные:</strong> 5 фильмов с полной информацией
          </p>
          <p>
            <strong>Реальный API:</strong> Подключение к серверу на localhost:8080
          </p>
        </div>
      </CardContent>
    </Card>
  )
}

"use client"

import { Button } from "@/components/ui/button"
import { Film, Award, BarChart3, Database, Globe } from "lucide-react"
import { cn } from "@/lib/utils"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { useState, useEffect } from "react"

interface SidebarProps {
  activePage: string
  onPageChange: (page: "movies" | "oscars" | "dashboard") => void
}

export function Sidebar({ activePage, onPageChange }: SidebarProps) {
  const [useMockData, setUseMockData] = useState(false)

  useEffect(() => {
    const currentValue = localStorage.getItem("useMockData") === "true"
    setUseMockData(currentValue)
  }, [])

  const handleToggle = (checked: boolean) => {
    setUseMockData(checked)
    localStorage.setItem("useMockData", checked.toString())
    // Reload page to apply changes
    if (typeof window !== "undefined") {
      window.location.reload()
    }
  }

  const menuItems = [
    {
      id: "movies" as const,
      label: "Фильмы",
      icon: Film,
    },
    {
      id: "oscars" as const,
      label: "Оскары",
      icon: Award,
    },
    {
      id: "dashboard" as const,
      label: "Статистика",
      icon: BarChart3,
    },
  ]

  return (
    <div className="w-64 bg-sidebar border-r border-sidebar-border flex flex-col">
      <div className="p-6">
        <h1 className="text-xl font-bold text-sidebar-foreground">Movies & Oscars</h1>
        <p className="text-sm text-sidebar-foreground/70 mt-1">Управление коллекцией</p>
      </div>

      <nav className="px-4 space-y-2">
        {menuItems.map((item) => {
          const Icon = item.icon
          return (
            <Button
              key={item.id}
              variant={activePage === item.id ? "default" : "ghost"}
              className={cn(
                "w-full justify-start",
                activePage === item.id
                  ? "bg-sidebar-primary text-sidebar-primary-foreground"
                  : "text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
              )}
              onClick={() => onPageChange(item.id)}
            >
              <Icon className="mr-2 h-4 w-4" />
              {item.label}
            </Button>
          )
        })}
      </nav>

      <div className="mt-auto p-4">
        <Card className="bg-sidebar-accent/50 border-sidebar-border">
          <CardHeader className="pb-2">
            <CardTitle className="flex items-center gap-2 text-sm text-sidebar-foreground">
              {useMockData ? (
                <Database className="h-4 w-4 text-blue-500" />
              ) : (
                <Globe className="h-4 w-4 text-green-500" />
              )}
              Источник данных
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-0">
            <div className="flex items-center justify-between">
              <Label htmlFor="mock-toggle" className="text-xs text-sidebar-foreground/70">
                {useMockData ? "Демо" : "API"}
              </Label>
              <Switch id="mock-toggle" checked={useMockData} onCheckedChange={handleToggle} />
            </div>
            <p className="text-xs text-sidebar-foreground/50 mt-2">
              {useMockData ? "Тестовые данные" : "localhost:8080"}
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

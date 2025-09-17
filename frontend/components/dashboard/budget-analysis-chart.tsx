"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, type TooltipProps } from "recharts"
import type { Movie } from "@/lib/api-client"

interface BudgetAnalysisChartProps {
  movies: Movie[]
}

export function BudgetAnalysisChart({ movies }: BudgetAnalysisChartProps) {
  // Filter movies with budget data and prepare scatter plot data
  const data = movies
    .filter((movie) => movie.budget && movie.budget > 0)
    .map((movie) => ({
      name: movie.name,
      budget: movie.budget! / 1000000, // Convert to millions
      oscars: movie.oscarsCount,
      genre: movie.genre,
    }))

  const genreLabels = {
    ACTION: "Боевик",
    ADVENTURE: "Приключения",
    TRAGEDY: "Трагедия",
    FANTASY: "Фэнтези",
  }

  interface PointData {
    name: string
    budget: number
    oscars: number
    genre: keyof typeof genreLabels
  }

  const CustomTooltip = ({ active, payload }: TooltipProps<number, string>) => {
    const point = payload && payload.length ? (payload[0]?.payload as PointData | undefined) : undefined
    if (active && point) {
      return (
        <div className="bg-background border border-border rounded-lg p-3 shadow-md">
          <p className="font-medium">{point.name}</p>
          <p className="text-sm text-muted-foreground">Бюджет: ${point.budget.toFixed(1)}M</p>
          <p className="text-sm text-muted-foreground">Оскары: {point.oscars}</p>
          <p className="text-sm text-muted-foreground">Жанр: {genreLabels[point.genre]}</p>
        </div>
      )
    }
    return null
  }

  const formatBudget = (value: number) => `$${value}M`

  return (
    <Card>
      <CardHeader>
        <CardTitle>Бюджет vs Количество Оскаров</CardTitle>
      </CardHeader>
      <CardContent>
        {data.length === 0 ? (
          <div className="h-80 flex items-center justify-center text-muted-foreground">
            Нет данных о бюджете фильмов
          </div>
        ) : (
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <ScatterChart data={data} margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis
                  type="number"
                  dataKey="budget"
                  name="Бюджет"
                  unit="M"
                  tickFormatter={formatBudget}
                  className="text-xs"
                />
                <YAxis type="number" dataKey="oscars" name="Оскары" className="text-xs" />
                <Tooltip content={<CustomTooltip />} />
                <Scatter dataKey="oscars" fill="hsl(var(--chart-2))" />
              </ScatterChart>
            </ResponsiveContainer>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

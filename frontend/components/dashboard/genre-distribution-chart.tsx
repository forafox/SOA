"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from "recharts"
import type { Movie } from "@/lib/api-client"

interface GenreDistributionChartProps {
  movies: Movie[]
}

export function GenreDistributionChart({ movies }: GenreDistributionChartProps) {
  const genreLabels = {
    ACTION: "Боевик",
    ADVENTURE: "Приключения",
    TRAGEDY: "Трагедия",
    FANTASY: "Фэнтези",
  }

  const genreStats = movies.reduce(
    (acc, movie) => {
      acc[movie.genre] = (acc[movie.genre] || 0) + 1
      return acc
    },
    {} as Record<string, number>,
  )

  const data = Object.entries(genreStats).map(([genre, count]) => ({
    name: genreLabels[genre as keyof typeof genreLabels],
    value: count,
    percentage: ((count / movies.length) * 100).toFixed(1),
  }))

  const COLORS = ["hsl(var(--chart-1))", "hsl(var(--chart-2))", "hsl(var(--chart-3))", "hsl(var(--chart-4))"]

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload
      return (
        <div className="bg-background border border-border rounded-lg p-3 shadow-md">
          <p className="font-medium">{data.name}</p>
          <p className="text-sm text-muted-foreground">
            {data.value} фильмов ({data.percentage}%)
          </p>
        </div>
      )
    }
    return null
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Распределение по жанрам</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={data}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percentage }) => `${name}: ${percentage}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {data.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip content={<CustomTooltip />} />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  )
}

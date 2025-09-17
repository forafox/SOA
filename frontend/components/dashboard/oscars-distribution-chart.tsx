"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, type TooltipProps } from "recharts"
import type { Movie } from "@/lib/api-client"

interface OscarsDistributionChartProps {
  movies: Movie[]
}

export function OscarsDistributionChart({ movies }: OscarsDistributionChartProps) {
  // Group movies by Oscar count
  const oscarStats = movies.reduce(
    (acc, movie) => {
      const count = movie.oscarsCount
      acc[count] = (acc[count] || 0) + 1
      return acc
    },
    {} as Record<number, number>,
  )

  const data = Object.entries(oscarStats)
    .map(([oscars, count]) => ({
      oscars: `${oscars} Оскар${Number.parseInt(oscars) === 1 ? "" : oscars === "2" || oscars === "3" || oscars === "4" ? "а" : "ов"}`,
      count,
      oscarsNum: Number.parseInt(oscars),
    }))
    .sort((a, b) => a.oscarsNum - b.oscarsNum)

  const CustomTooltip = ({ active, payload, label }: TooltipProps<number, string>) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-background border border-border rounded-lg p-3 shadow-md">
          <p className="font-medium">{label}</p>
          <p className="text-sm text-muted-foreground">{payload[0].value} фильмов</p>
        </div>
      )
    }
    return null
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Распределение по количеству Оскаров</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
              <XAxis dataKey="oscars" className="text-xs" />
              <YAxis className="text-xs" />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="count" fill="hsl(var(--chart-1))" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  )
}

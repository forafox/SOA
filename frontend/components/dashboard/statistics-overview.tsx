"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Film, Award, DollarSign, Calendar, Trophy, Star } from "lucide-react"
import type { Movie } from "@/lib/api-client"

interface StatisticsOverviewProps {
  movies: Movie[]
}

export function StatisticsOverview({ movies }: StatisticsOverviewProps) {
  const totalMovies = movies.length
  const totalOscars = movies.reduce((sum, movie) => sum + movie.oscarsCount, 0)
  const totalGoldenPalms = movies.reduce((sum, movie) => sum + (movie.goldenPalmCount || 0), 0)
  const totalBudget = movies.reduce((sum, movie) => sum + (movie.budget || 0), 0)
  const averageOscars = totalMovies > 0 ? (totalOscars / totalMovies).toFixed(1) : "0"
  const averageBudget = totalMovies > 0 ? totalBudget / totalMovies : 0

  const genreStats = movies.reduce(
    (acc, movie) => {
      acc[movie.genre] = (acc[movie.genre] || 0) + 1
      return acc
    },
    {} as Record<string, number>,
  )

  const mostPopularGenre = Object.entries(genreStats).reduce(
    (max, [genre, count]) => (count > max.count ? { genre, count } : max),
    { genre: "", count: 0 },
  )

  const genreLabels = {
    ACTION: "Боевик",
    ADVENTURE: "Приключения",
    TRAGEDY: "Трагедия",
    FANTASY: "Фэнтези",
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("ru-RU", {
      style: "currency",
      currency: "USD",
      notation: "compact",
      maximumFractionDigits: 1,
    }).format(amount)
  }

  const currentYear = new Date().getFullYear()
  const recentMovies = movies.filter((movie) => new Date(movie.creationDate).getFullYear() === currentYear).length

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Всего фильмов</CardTitle>
          <Film className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{totalMovies}</div>
          <p className="text-xs text-muted-foreground">
            {recentMovies} добавлено в {currentYear} году
          </p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Всего Оскаров</CardTitle>
          <Award className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{totalOscars}</div>
          <p className="text-xs text-muted-foreground">В среднем {averageOscars} на фильм</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Общий бюджет</CardTitle>
          <DollarSign className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{formatCurrency(totalBudget)}</div>
          <p className="text-xs text-muted-foreground">Средний: {formatCurrency(averageBudget)}</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Популярный жанр</CardTitle>
          <Star className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{mostPopularGenre.count}</div>
          <p className="text-xs text-muted-foreground">
            {genreLabels[mostPopularGenre.genre as keyof typeof genreLabels] || "Нет данных"}
          </p>
        </CardContent>
      </Card>

      <Card className="md:col-span-2 lg:col-span-2">
        <CardHeader>
          <CardTitle className="text-sm font-medium flex items-center gap-2">
            <Trophy className="h-4 w-4" />
            Награды
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-between">
            <div>
              <div className="text-2xl font-bold">{totalGoldenPalms}</div>
              <p className="text-xs text-muted-foreground">Золотых пальм</p>
            </div>
            <div className="flex gap-2">
              <Badge variant="secondary">{totalOscars} Оскаров</Badge>
              <Badge variant="outline">{totalGoldenPalms} Пальм</Badge>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card className="md:col-span-2 lg:col-span-2">
        <CardHeader>
          <CardTitle className="text-sm font-medium flex items-center gap-2">
            <Calendar className="h-4 w-4" />
            Распределение по жанрам
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            {Object.entries(genreStats)
              .sort(([, a], [, b]) => b - a)
              .map(([genre, count]) => (
                <div key={genre} className="flex items-center justify-between">
                  <span className="text-sm">{genreLabels[genre as keyof typeof genreLabels]}</span>
                  <div className="flex items-center gap-2">
                    <div className="w-20 bg-muted rounded-full h-2">
                      <div
                        className="bg-primary h-2 rounded-full"
                        style={{ width: `${(count / totalMovies) * 100}%` }}
                      />
                    </div>
                    <span className="text-sm font-medium w-8 text-right">{count}</span>
                  </div>
                </div>
              ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

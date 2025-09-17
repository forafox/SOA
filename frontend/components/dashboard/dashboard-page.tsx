"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { BarChart3, PieChart, TrendingUp, RefreshCw } from "lucide-react"
import { StatisticsOverview } from "./statistics-overview"
import { GenreDistributionChart } from "./genre-distribution-chart"
import { OscarsDistributionChart } from "./oscars-distribution-chart"
import { BudgetAnalysisChart } from "./budget-analysis-chart"
import { TopMoviesTable } from "./top-movies-table"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie } from "@/lib/api-client"

export function DashboardPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<unknown>(null)
  const [lastUpdated, setLastUpdated] = useState<Date>(new Date())

  const loadData = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await apiClient.getMovies({}, { size: 1000 }) // Get all movies for analytics
      setMovies(data || [])
      setLastUpdated(new Date())
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  if (error) {
    return (
      <div className="p-6">
        <ErrorDisplay error={error} title="Ошибка загрузки данных" />
        <Button onClick={loadData} className="mt-4">
          Попробовать снова
        </Button>
      </div>
    )
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-balance">Статистика и аналитика</h1>
          <p className="text-muted-foreground">
            Визуализация данных коллекции фильмов и наград
            {!loading && (
              <span className="block text-sm mt-1">Последнее обновление: {lastUpdated.toLocaleString("ru-RU")}</span>
            )}
          </p>
        </div>
        <Button onClick={loadData} disabled={loading} variant="outline">
          <RefreshCw className={`mr-2 h-4 w-4 ${loading ? "animate-spin" : ""}`} />
          Обновить данные
        </Button>
      </div>

      {loading ? (
        <div className="flex justify-center py-12">
          <LoadingSpinner size="lg" />
        </div>
      ) : (
        <>
          <StatisticsOverview movies={movies} />

          <Tabs defaultValue="charts" className="space-y-6">
            <TabsList className="grid w-full grid-cols-3">
              <TabsTrigger value="charts" className="flex items-center gap-2">
                <BarChart3 className="h-4 w-4" />
                Графики
              </TabsTrigger>
              <TabsTrigger value="distribution" className="flex items-center gap-2">
                <PieChart className="h-4 w-4" />
                Распределение
              </TabsTrigger>
              <TabsTrigger value="top" className="flex items-center gap-2">
                <TrendingUp className="h-4 w-4" />
                Топ фильмы
              </TabsTrigger>
            </TabsList>

            <TabsContent value="charts" className="space-y-6">
              <div className="grid gap-6 md:grid-cols-2">
                <OscarsDistributionChart movies={movies} />
                <BudgetAnalysisChart movies={movies} />
              </div>
            </TabsContent>

            <TabsContent value="distribution" className="space-y-6">
              <GenreDistributionChart movies={movies} />
            </TabsContent>

            <TabsContent value="top" className="space-y-6">
              <TopMoviesTable movies={movies} />
            </TabsContent>
          </Tabs>
        </>
      )}
    </div>
  )
}

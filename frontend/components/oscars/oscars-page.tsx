"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Award, Users, TrendingUp } from "lucide-react"
import { OscarLosersCard } from "./oscar-losers-card"
import { HonorMoviesCard } from "./honor-movies-card"
import { MovieOscarsCard } from "./movie-oscars-card"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie } from "@/lib/api-client"

export function OscarsPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<unknown>(null)

  const loadMovies = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await apiClient.getMovies()
      setMovies(data || [])
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMovies()
  }, [])

  if (error) {
    return (
      <div className="p-6">
        <ErrorDisplay error={error} title="Ошибка загрузки данных" />
        <Button onClick={loadMovies} className="mt-4">
          Попробовать снова
        </Button>
      </div>
    )
  }

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-balance">Управление Оскарами</h1>
        <p className="text-muted-foreground">Награждение фильмов, статистика и специальные операции</p>
      </div>

      <Tabs defaultValue="honor" className="space-y-6">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="honor" className="flex items-center gap-2">
            <Award className="h-4 w-4" />
            Награждение
          </TabsTrigger>
          <TabsTrigger value="losers" className="flex items-center gap-2">
            <Users className="h-4 w-4" />
            Операторы без Оскаров
          </TabsTrigger>
          <TabsTrigger value="movies" className="flex items-center gap-2">
            <TrendingUp className="h-4 w-4" />
            Оскары по фильмам
          </TabsTrigger>
        </TabsList>

        <TabsContent value="honor" className="space-y-6">
          {loading ? (
            <div className="flex justify-center py-8">
              <LoadingSpinner size="lg" />
            </div>
          ) : (
            <HonorMoviesCard movies={movies} onMoviesUpdated={setMovies} />
          )}
        </TabsContent>

        <TabsContent value="losers">
          <OscarLosersCard />
        </TabsContent>

        <TabsContent value="movies" className="space-y-6">
          {loading ? (
            <div className="flex justify-center py-8">
              <LoadingSpinner size="lg" />
            </div>
          ) : (
            <MovieOscarsCard movies={movies} onMoviesUpdated={setMovies} />
          )}
        </TabsContent>
      </Tabs>
    </div>
  )
}

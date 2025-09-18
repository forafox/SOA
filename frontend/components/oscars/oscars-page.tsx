"use client"

import { useState, useEffect, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Award, Users, TrendingUp, ChevronLeft, ChevronRight } from "lucide-react"
import { OscarLosersCard } from "./oscar-losers-card"
import { HonorMoviesCard } from "./honor-movies-card"
import { MovieOscarsCard } from "./movie-oscars-card"
import { CallbackStatus } from "./callback-status"
import { CallbackTester } from "./callback-tester"
import { CallbackPending } from "./callback-pending"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie, type PaginationParams } from "@/lib/api-client"

export function OscarsPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<unknown>(null)
  const [pagination, setPagination] = useState<PaginationParams>({ page: 1, size: 20 })
  const [totalMovies, setTotalMovies] = useState(0)

  const loadMovies = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await apiClient.getMovies({}, pagination)
      setMovies(data || [])
      // Для простоты считаем, что если получили меньше чем size, то это последняя страница
      const pageSize = pagination.size || 20
      const currentPage = pagination.page || 1
      if (data && data.length < pageSize) {
        setTotalMovies((currentPage - 1) * pageSize + data.length)
      } else {
        setTotalMovies((currentPage * pageSize) + 1) // +1 чтобы показать, что есть еще
      }
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }, [pagination])

  const handlePageChange = (newPage: number) => {
    setPagination(prev => ({ ...prev, page: newPage }))
  }

  useEffect(() => {
    loadMovies()
  }, [loadMovies])

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
        <TabsList className="grid w-full grid-cols-4">
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
          <TabsTrigger value="callbacks" className="flex items-center gap-2">
            <Award className="h-4 w-4" />
            Коллбэки
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
            <>
              <MovieOscarsCard movies={movies} onMoviesUpdated={setMovies} />
              
              {/* Пагинация для списка фильмов */}
              {movies.length > 0 && (
                <div className="flex items-center justify-between mt-6">
                  <div className="text-sm text-muted-foreground">
                    Показано {movies.length} из {totalMovies} фильмов
                  </div>
                  <div className="flex items-center space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handlePageChange((pagination.page || 1) - 1)}
                      disabled={(pagination.page || 1) <= 1}
                    >
                      <ChevronLeft className="h-4 w-4" />
                      Назад
                    </Button>
                    <span className="text-sm">
                      Страница {pagination.page || 1}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handlePageChange((pagination.page || 1) + 1)}
                      disabled={movies.length < (pagination.size || 20)}
                    >
                      Вперед
                      <ChevronRight className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </TabsContent>

        <TabsContent value="callbacks" className="space-y-6">
          <CallbackPending />
          <CallbackTester />
          <CallbackStatus />
        </TabsContent>
      </Tabs>
    </div>
  )
}

"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Plus, Search } from "lucide-react"
import { MoviesTable } from "./movies-table"
import { CreateMovieDialog } from "./create-movie-dialog"
import { EditMovieDialog } from "./edit-movie-dialog"
import { AdvancedFilters } from "./advanced-filters"
import { SpecialSearchCard } from "./special-search-card"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie, type MovieFilters, type PaginationParams } from "@/lib/api-client"
import { useToast } from "@/hooks/use-toast"

export function MoviesPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<unknown>(null)
  const [searchTerm, setSearchTerm] = useState("")
  const [filters, setFilters] = useState<MovieFilters>({})
  const [pagination, setPagination] = useState<PaginationParams>({ page: 1, size: 20 })
  const [createDialogOpen, setCreateDialogOpen] = useState(false)
  const [editingMovie, setEditingMovie] = useState<Movie | null>(null)
  const [specialSearchResults, setSpecialSearchResults] = useState<{
    type: string
    results: Movie[] | { count: number }
    query: string
  } | null>(null)
  const { toast } = useToast()

  const loadMovies = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await apiClient.getMovies(filters, pagination)
      setMovies(data || [])
      setSpecialSearchResults(null)
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMovies()
  }, [filters, pagination])

  const handleSearch = () => {
    setFilters({ ...filters, name: searchTerm })
  }

  const handleSpecialSearch = (type: string, results: Movie[] | { count: number }, query: string) => {
    setSpecialSearchResults({ type, results, query })
    setMovies([]) // Clear regular movies when showing special results
  }

  const handleMovieCreated = (movie: Movie) => {
    setMovies([movie, ...movies])
    setCreateDialogOpen(false)
    toast({
      title: "Успех",
      description: "Фильм успешно создан",
    })
  }

  const handleMovieUpdated = (updatedMovie: Movie) => {
    setMovies(movies.map((m) => (m.id === updatedMovie.id ? updatedMovie : m)))
    setEditingMovie(null)
    toast({
      title: "Успех",
      description: "Фильм успешно обновлен",
    })
  }

  const handleMovieDeleted = (movieId: number) => {
    setMovies(movies.filter((m) => m.id !== movieId))
    toast({
      title: "Успех",
      description: "Фильм успешно удален",
    })
  }

  if (error) {
    return (
      <div className="p-6">
        <ErrorDisplay error={error} title="Ошибка загрузки фильмов" />
        <Button onClick={loadMovies} className="mt-4">
          Попробовать снова
        </Button>
      </div>
    )
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-balance">Управление фильмами</h1>
          <p className="text-muted-foreground">Создавайте, редактируйте и управляйте коллекцией фильмов</p>
        </div>
        <Button onClick={() => setCreateDialogOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          Добавить фильм
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Поиск и фильтры</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex gap-4">
            <div className="flex-1">
              <Input
                placeholder="Поиск по названию фильма..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              />
            </div>
            <Button onClick={handleSearch}>
              <Search className="mr-2 h-4 w-4" />
              Поиск
            </Button>
          </div>
        </CardContent>
      </Card>

      <AdvancedFilters
        filters={filters}
        onFiltersChange={setFilters}
        pagination={pagination}
        onPaginationChange={setPagination}
        onClearFilters={() => {
          setFilters({})
          setSearchTerm("")
          setPagination({ page: 1, size: 20 })
          setSpecialSearchResults(null)
        }}
      />

      <SpecialSearchCard onSpecialSearch={handleSpecialSearch} />

      <Card>
        <CardHeader>
          <CardTitle>
            {specialSearchResults
              ? `Результаты: ${specialSearchResults.type} "${specialSearchResults.query}"`
              : "Список фильмов"}
          </CardTitle>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex justify-center py-8">
              <LoadingSpinner size="lg" />
            </div>
          ) : specialSearchResults ? (
            <div className="space-y-4">
              {typeof specialSearchResults.results === "object" && "count" in specialSearchResults.results ? (
                <div className="text-center py-8">
                  <div className="text-4xl font-bold text-primary">{specialSearchResults.results.count}</div>
                  <div className="text-muted-foreground mt-2">{specialSearchResults.type}</div>
                </div>
              ) : (
                <MoviesTable
                  movies={specialSearchResults.results as Movie[]}
                  onEdit={setEditingMovie}
                  onDelete={handleMovieDeleted}
                />
              )}
              <Button onClick={loadMovies} variant="outline">
                Вернуться к полному списку
              </Button>
            </div>
          ) : (
            <MoviesTable movies={movies} onEdit={setEditingMovie} onDelete={handleMovieDeleted} />
          )}
        </CardContent>
      </Card>

      <CreateMovieDialog
        open={createDialogOpen}
        onOpenChange={setCreateDialogOpen}
        onMovieCreated={handleMovieCreated}
      />

      {editingMovie && (
        <EditMovieDialog
          movie={editingMovie}
          open={!!editingMovie}
          onOpenChange={(open) => !open && setEditingMovie(null)}
          onMovieUpdated={handleMovieUpdated}
        />
      )}
    </div>
  )
}

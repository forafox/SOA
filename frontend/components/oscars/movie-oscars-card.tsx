"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Plus, Trash2, Eye } from "lucide-react"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie, type OscarAward } from "@/lib/api-client"
import { useToast } from "@/hooks/use-toast"
import { callbackService } from "@/lib/callback-service"

interface MovieOscarsCardProps {
  movies: Movie[]
  onMoviesUpdated: (movies: Movie[]) => void
}

export function MovieOscarsCard({ movies, onMoviesUpdated }: MovieOscarsCardProps) {
  const [selectedMovieId, setSelectedMovieId] = useState<string>("")
  const [oscarsToAdd, setOscarsToAdd] = useState("")
  const [movieOscars, setMovieOscars] = useState<OscarAward[]>([])
  const [loading, setLoading] = useState({ add: false, view: false, delete: false })
  const [error, setError] = useState<unknown>(null)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const { toast } = useToast()

  // Инициализируем callback service с toast
  callbackService.setToast(toast)

  const selectedMovie = movies.find((m) => m.id.toString() === selectedMovieId)

  const handleAddOscars = async () => {
    if (!selectedMovieId || !oscarsToAdd) {
      toast({
        title: "Ошибка",
        description: "Выберите фильм и укажите количество Оскаров",
        variant: "destructive",
      })
      return
    }

    try {
      setLoading((prev) => ({ ...prev, add: true }))
      setError(null)
      
      // Отправляем событие о начале операции
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('operation-start', {
          detail: { operationId: 'notifyOscarsTeam' }
        }))
      }
      
      const callbackUrls = callbackService.getCallbackUrls()
      const result = await apiClient.addOscarsToMovie(
        Number.parseInt(selectedMovieId), 
        Number.parseInt(oscarsToAdd),
        callbackUrls.notifyOscarsTeam
      )

      // Update movies list
      const updatedMovies = movies.map((movie) => {
        const updatedMovie = result.updatedMovies.find((um) => um.id === movie.id)
        return updatedMovie || movie
      })
      onMoviesUpdated(updatedMovies)

      toast({
        title: "Успех",
        description: `Добавлено ${result.updatedCount || oscarsToAdd} Оскаров фильму "${selectedMovie?.name}"`,
      })

      setOscarsToAdd("")
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, add: false }))
    }
  }

  const handleViewOscars = async () => {
    if (!selectedMovieId) return

    try {
      setLoading((prev) => ({ ...prev, view: true }))
      setError(null)
      const oscars = await apiClient.getOscarsByMovie(Number.parseInt(selectedMovieId))
      setMovieOscars(oscars || [])
    } catch (err) {
      setError(err)
      setMovieOscars([])
    } finally {
      setLoading((prev) => ({ ...prev, view: false }))
    }
  }

  const handleDeleteOscars = async () => {
    if (!selectedMovieId) return

    try {
      setLoading((prev) => ({ ...prev, delete: true }))
      setError(null)
      await apiClient.deleteOscarsByMovie(Number.parseInt(selectedMovieId))

      // Refresh movies to get updated oscar counts
      const updatedMovies = await apiClient.getMovies()
      onMoviesUpdated(updatedMovies || [])

      setMovieOscars([])
      setDeleteDialogOpen(false)

      toast({
        title: "Успех",
        description: `Все Оскары удалены у фильма "${selectedMovie?.name}"`,
      })
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, delete: false }))
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ru-RU")
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Управление Оскарами по фильмам</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {!!error && <ErrorDisplay error={error} title="Ошибка операции" />}

          <div className="grid gap-4 md:grid-cols-3">
            <div>
              <Label htmlFor="movieSelect">Выберите фильм</Label>
              <Select value={selectedMovieId} onValueChange={setSelectedMovieId}>
                <SelectTrigger>
                  <SelectValue placeholder="Выберите фильм..." />
                </SelectTrigger>
                <SelectContent>
                  {movies.map((movie) => (
                    <SelectItem key={movie.id} value={movie.id.toString()}>
                      {movie.name} ({movie.oscarsCount} Оскаров)
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label htmlFor="oscarsToAdd">Количество Оскаров для добавления</Label>
              <Input
                id="oscarsToAdd"
                type="number"
                min={1}
                placeholder="Например: 2"
                value={oscarsToAdd}
                onChange={(e) => setOscarsToAdd(e.target.value)}
              />
            </div>

            <div className="flex items-end">
              <Button onClick={handleAddOscars} disabled={loading.add || !selectedMovieId} className="w-full">
                {loading.add ? (
                  <>
                    <LoadingSpinner size="sm" className="mr-2" />
                    Добавление...
                  </>
                ) : (
                  <>
                    <Plus className="mr-2 h-4 w-4" />
                    Добавить Оскары
                  </>
                )}
              </Button>
            </div>
          </div>

          {selectedMovie && (
            <>
              <Separator />
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-semibold">{selectedMovie.name}</h3>
                  <div className="flex items-center gap-2 mt-1">
                    <Badge variant="secondary">{selectedMovie.oscarsCount} Оскаров</Badge>
                    <Badge variant="outline">{selectedMovie.genre}</Badge>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button onClick={handleViewOscars} disabled={loading.view} variant="outline" size="sm">
                    {loading.view ? <LoadingSpinner size="sm" className="mr-2" /> : <Eye className="mr-2 h-4 w-4" />}
                    Просмотр Оскаров
                  </Button>
                  <Button
                    onClick={() => setDeleteDialogOpen(true)}
                    disabled={loading.delete}
                    variant="destructive"
                    size="sm"
                  >
                    <Trash2 className="mr-2 h-4 w-4" />
                    Удалить все Оскары
                  </Button>
                </div>
              </div>
            </>
          )}
        </CardContent>
      </Card>

      {movieOscars.length > 0 && (
        <Card>
          <CardHeader>
          <CardTitle>Оскары фильма &quot;{selectedMovie?.name}&quot;</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID награды</TableHead>
                    <TableHead>Дата</TableHead>
                    <TableHead>Категория</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {movieOscars.map((oscar) => (
                    <TableRow key={oscar.awardId}>
                      <TableCell className="font-mono">{oscar.awardId}</TableCell>
                      <TableCell>{formatDate(oscar.date)}</TableCell>
                      <TableCell>
                        <Badge variant="secondary">{oscar.category}</Badge>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      )}

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Подтвердите удаление</AlertDialogTitle>
            <AlertDialogDescription>
              Вы уверены, что хотите удалить все Оскары у фильма &quot;{selectedMovie?.name}&quot;? Это действие нельзя отменить.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={loading.delete}>Отмена</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteOscars}
              disabled={loading.delete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {loading.delete ? "Удаление..." : "Удалить все Оскары"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}

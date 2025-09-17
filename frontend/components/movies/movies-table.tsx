"use client"

import { useState } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Edit, Trash2, MoreHorizontal, ArrowUpDown, ArrowUp, ArrowDown } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
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
import { type Movie, apiClient } from "@/lib/api-client"
import { ErrorDisplay } from "@/components/error-display"
import { useToast } from "@/hooks/use-toast"

interface MoviesTableProps {
  movies: Movie[]
  onEdit: (movie: Movie) => void
  onDelete: (movieId: number) => void
}

type SortField = "name" | "genre" | "oscarsCount" | "goldenPalmCount" | "budget" | "creationDate"
type SortDirection = "asc" | "desc" | null

export function MoviesTable({ movies, onEdit, onDelete }: MoviesTableProps) {
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [movieToDelete, setMovieToDelete] = useState<Movie | null>(null)
  const [deleting, setDeleting] = useState(false)
  const [error, setError] = useState<unknown>(null)
  const [sortField, setSortField] = useState<SortField | null>(null)
  const [sortDirection, setSortDirection] = useState<SortDirection>(null)
  const { toast } = useToast()

  const genreLabels = {
    ACTION: "Боевик",
    ADVENTURE: "Приключения",
    TRAGEDY: "Трагедия",
    FANTASY: "Фэнтези",
  }

  const handleSort = (field: SortField) => {
    if (sortField === field) {
      if (sortDirection === "asc") {
        setSortDirection("desc")
      } else if (sortDirection === "desc") {
        setSortField(null)
        setSortDirection(null)
      } else {
        setSortDirection("asc")
      }
    } else {
      setSortField(field)
      setSortDirection("asc")
    }
  }

  const getSortIcon = (field: SortField) => {
    if (sortField !== field) return <ArrowUpDown className="h-4 w-4" />
    if (sortDirection === "asc") return <ArrowUp className="h-4 w-4" />
    if (sortDirection === "desc") return <ArrowDown className="h-4 w-4" />
    return <ArrowUpDown className="h-4 w-4" />
  }

  const sortedMovies = [...movies].sort((a, b) => {
    if (!sortField || !sortDirection) return 0

    let aValue: any = a[sortField]
    let bValue: any = b[sortField]

    // Handle nested properties
    if (sortField === "goldenPalmCount") {
      aValue = a.goldenPalmCount || 0
      bValue = b.goldenPalmCount || 0
    } else if (sortField === "budget") {
      aValue = a.budget || 0
      bValue = b.budget || 0
    }

    // Convert to comparable values
    if (typeof aValue === "string") {
      aValue = aValue.toLowerCase()
      bValue = bValue.toLowerCase()
    }

    if (aValue < bValue) return sortDirection === "asc" ? -1 : 1
    if (aValue > bValue) return sortDirection === "asc" ? 1 : -1
    return 0
  })

  const handleDeleteClick = (movie: Movie) => {
    setMovieToDelete(movie)
    setDeleteDialogOpen(true)
  }

  const handleDeleteConfirm = async () => {
    if (!movieToDelete) return

    try {
      setDeleting(true)
      setError(null)
      await apiClient.deleteMovie(movieToDelete.id)
      onDelete(movieToDelete.id)
      setDeleteDialogOpen(false)
      setMovieToDelete(null)
    } catch (err) {
      setError(err)
      toast({
        title: "Ошибка",
        description: "Не удалось удалить фильм",
        variant: "destructive",
      })
    } finally {
      setDeleting(false)
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ru-RU")
  }

  const formatCurrency = (amount?: number) => {
    if (!amount) return "Не указан"
    return new Intl.NumberFormat("ru-RU", {
      style: "currency",
      currency: "USD",
    }).format(amount)
  }

  if (movies.length === 0) {
    return <div className="text-center py-8 text-muted-foreground">Фильмы не найдены</div>
  }

  return (
    <>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>
                <Button
                  variant="ghost"
                  className="h-auto p-0 font-semibold hover:bg-transparent"
                  onClick={() => handleSort("name")}
                >
                  Название
                  {getSortIcon("name")}
                </Button>
              </TableHead>
              <TableHead>
                <Button
                  variant="ghost"
                  className="h-auto p-0 font-semibold hover:bg-transparent"
                  onClick={() => handleSort("genre")}
                >
                  Жанр
                  {getSortIcon("genre")}
                </Button>
              </TableHead>
              <TableHead>
                <Button
                  variant="ghost"
                  className="h-auto p-0 font-semibold hover:bg-transparent"
                  onClick={() => handleSort("oscarsCount")}
                >
                  Оскары
                  {getSortIcon("oscarsCount")}
                </Button>
              </TableHead>
              <TableHead>
                <Button
                  variant="ghost"
                  className="h-auto p-0 font-semibold hover:bg-transparent"
                  onClick={() => handleSort("goldenPalmCount")}
                >
                  Золотые пальмы
                  {getSortIcon("goldenPalmCount")}
                </Button>
              </TableHead>
              <TableHead>
                <Button
                  variant="ghost"
                  className="h-auto p-0 font-semibold hover:bg-transparent"
                  onClick={() => handleSort("budget")}
                >
                  Бюджет
                  {getSortIcon("budget")}
                </Button>
              </TableHead>
              <TableHead>Сценарист</TableHead>
              <TableHead>
                <Button
                  variant="ghost"
                  className="h-auto p-0 font-semibold hover:bg-transparent"
                  onClick={() => handleSort("creationDate")}
                >
                  Дата создания
                  {getSortIcon("creationDate")}
                </Button>
              </TableHead>
              <TableHead className="w-[70px]">Действия</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {sortedMovies.map((movie) => (
              <TableRow key={movie.id}>
                <TableCell className="font-medium">{movie.name}</TableCell>
                <TableCell>
                  <Badge variant="secondary">{genreLabels[movie.genre]}</Badge>
                </TableCell>
                <TableCell>{movie.oscarsCount}</TableCell>
                <TableCell>{movie.goldenPalmCount || "Нет"}</TableCell>
                <TableCell>{formatCurrency(movie.budget)}</TableCell>
                <TableCell>{movie.screenwriter.name}</TableCell>
                <TableCell>{formatDate(movie.creationDate)}</TableCell>
                <TableCell>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="ghost" className="h-8 w-8 p-0">
                        <MoreHorizontal className="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem onClick={() => onEdit(movie)}>
                        <Edit className="mr-2 h-4 w-4" />
                        Редактировать
                      </DropdownMenuItem>
                      <DropdownMenuItem onClick={() => handleDeleteClick(movie)} className="text-destructive">
                        <Trash2 className="mr-2 h-4 w-4" />
                        Удалить
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Подтвердите удаление</AlertDialogTitle>
            <AlertDialogDescription>
              Вы уверены, что хотите удалить фильм "{movieToDelete?.name}"? Это действие нельзя отменить.
            </AlertDialogDescription>
          </AlertDialogHeader>
          {!!error && (
            <div className="my-4">
              <ErrorDisplay error={error} title="Ошибка удаления" />
            </div>
          )}
          <AlertDialogFooter>
            <AlertDialogCancel disabled={deleting}>Отмена</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteConfirm}
              disabled={deleting}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {deleting ? "Удаление..." : "Удалить"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}

"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie } from "@/lib/api-client"

interface EditMovieDialogProps {
  movie: Movie
  open: boolean
  onOpenChange: (open: boolean) => void
  onMovieUpdated: (movie: Movie) => void
}

export function EditMovieDialog({ movie, open, onOpenChange, onMovieUpdated }: EditMovieDialogProps) {
  const [formData, setFormData] = useState<Partial<Movie>>({})
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<unknown>(null)

  useEffect(() => {
    if (movie) {
      setFormData({
        name: movie.name,
        coordinates: movie.coordinates,
        oscarsCount: movie.oscarsCount,
        goldenPalmCount: movie.goldenPalmCount,
        budget: movie.budget,
        genre: movie.genre,
        screenwriter: movie.screenwriter,
      })
    }
  }, [movie])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      setLoading(true)
      setError(null)
      const updatedMovie = await apiClient.updateMovie(movie.id, formData)
      if (updatedMovie) {
        onMovieUpdated(updatedMovie)
      }
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  const updateFormData = (field: string, value: string | number | undefined) => {
    if (field.startsWith("coordinates.")) {
      const coordField = field.split(".")[1]
      setFormData((prev) => ({
        ...prev,
        coordinates: {
          ...prev.coordinates!,
          [coordField]: value,
        },
      }))
    } else if (field.startsWith("screenwriter.")) {
      const writerField = field.split(".")[1]
      setFormData((prev) => ({
        ...prev,
        screenwriter: {
          ...prev.screenwriter!,
          [writerField]: value,
        },
      }))
    } else {
      setFormData((prev) => ({ ...prev, [field]: value }))
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Редактировать фильм</DialogTitle>
          <DialogDescription>Измените необходимые поля и сохраните изменения</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {!!error && <ErrorDisplay error={error} title="Ошибка обновления фильма" />}

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Основная информация</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="name">Название фильма</Label>
                <Input id="name" value={formData.name || ""} onChange={(e) => updateFormData("name", e.target.value)} />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="x">Координата X</Label>
                  <Input
                    id="x"
                    type="number"
                    value={formData.coordinates?.x || 0}
                    onChange={(e) => updateFormData("coordinates.x", Number.parseInt(e.target.value))}
                  />
                </div>
                <div>
                  <Label htmlFor="y">Координата Y (макс. 558)</Label>
                  <Input
                    id="y"
                    type="number"
                    max={558}
                    value={formData.coordinates?.y || 0}
                    onChange={(e) => updateFormData("coordinates.y", Number.parseFloat(e.target.value))}
                  />
                </div>
              </div>

              <div>
                <Label htmlFor="genre">Жанр</Label>
                <Select value={formData.genre} onValueChange={(value) => updateFormData("genre", value)}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ACTION">Боевик</SelectItem>
                    <SelectItem value="ADVENTURE">Приключения</SelectItem>
                    <SelectItem value="TRAGEDY">Трагедия</SelectItem>
                    <SelectItem value="FANTASY">Фэнтези</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="oscarsCount">Количество Оскаров (мин. 1)</Label>
                  <Input
                    id="oscarsCount"
                    type="number"
                    min={1}
                    value={formData.oscarsCount || 1}
                    onChange={(e) => updateFormData("oscarsCount", Number.parseInt(e.target.value))}
                  />
                </div>
                <div>
                  <Label htmlFor="goldenPalmCount">Золотые пальмы (мин. 1)</Label>
                  <Input
                    id="goldenPalmCount"
                    type="number"
                    min={1}
                    value={formData.goldenPalmCount || ""}
                    onChange={(e) =>
                      updateFormData("goldenPalmCount", e.target.value ? Number.parseInt(e.target.value) : undefined)
                    }
                  />
                </div>
              </div>

              <div>
                <Label htmlFor="budget">Бюджет (больше 0)</Label>
                <Input
                  id="budget"
                  type="number"
                  min={0.01}
                  step={0.01}
                  value={formData.budget || ""}
                  onChange={(e) =>
                    updateFormData("budget", e.target.value ? Number.parseFloat(e.target.value) : undefined)
                  }
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Информация о сценаристе</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="screenwriterName">Имя сценариста</Label>
                <Input
                  id="screenwriterName"
                  value={formData.screenwriter?.name || ""}
                  onChange={(e) => updateFormData("screenwriter.name", e.target.value)}
                />
              </div>

              <div>
                <Label htmlFor="screenwriterBirthday">Дата рождения</Label>
                <Input
                  id="screenwriterBirthday"
                  type="date"
                  value={formData.screenwriter?.birthday || ""}
                  onChange={(e) => updateFormData("screenwriter.birthday", e.target.value)}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="screenwriterHeight">Рост (больше 0)</Label>
                  <Input
                    id="screenwriterHeight"
                    type="number"
                    min={0.01}
                    step={0.01}
                    value={formData.screenwriter?.height || 0}
                    onChange={(e) => updateFormData("screenwriter.height", Number.parseFloat(e.target.value))}
                  />
                </div>
                <div>
                  <Label htmlFor="screenwriterWeight">Вес (мин. 1)</Label>
                  <Input
                    id="screenwriterWeight"
                    type="number"
                    min={1}
                    value={formData.screenwriter?.weight || 1}
                    onChange={(e) => updateFormData("screenwriter.weight", Number.parseInt(e.target.value))}
                  />
                </div>
              </div>

              <div>
                <Label htmlFor="screenwriterPassportID">ID паспорта</Label>
                <Input
                  id="screenwriterPassportID"
                  value={formData.screenwriter?.passportID || ""}
                  onChange={(e) => updateFormData("screenwriter.passportID", e.target.value)}
                />
              </div>
            </CardContent>
          </Card>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={loading}>
              Отмена
            </Button>
            <Button type="submit" disabled={loading}>
              {loading ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Сохранение...
                </>
              ) : (
                "Сохранить изменения"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

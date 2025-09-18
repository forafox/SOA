"use client"

import type React from "react"

import { useState } from "react"
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
import { apiClient, type CreateMovieData, type Movie } from "@/lib/api-client"

interface CreateMovieDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onMovieCreated: (movie: Movie) => void
}

export function CreateMovieDialog({ open, onOpenChange, onMovieCreated }: CreateMovieDialogProps) {
  const [formData, setFormData] = useState<CreateMovieData>({
    name: "",
    x: 0,
    y: 0,
    oscarsCount: 1,
    goldenPalmCount: undefined,
    budget: undefined,
    genre: "ACTION",
    screenwriterName: "",
    screenwriterBirthday: "",
    screenwriterHeight: 0,
    screenwriterWeight: 1,
    screenwriterPassportID: "",
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<unknown>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      setLoading(true)
      setError(null)
      const movie = await apiClient.createMovie(formData)
      onMovieCreated(movie)
      // Reset form
      setFormData({
        name: "",
        x: 0,
        y: 0,
        oscarsCount: 1,
        goldenPalmCount: undefined,
        budget: undefined,
        genre: "ACTION",
        screenwriterName: "",
        screenwriterBirthday: "",
        screenwriterHeight: 0,
        screenwriterWeight: 1,
        screenwriterPassportID: "",
      })
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  const updateFormData = (field: keyof CreateMovieData, value: string | number | undefined) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Создать новый фильм</DialogTitle>
          <DialogDescription>Заполните все обязательные поля для создания фильма</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {!!error && <ErrorDisplay error={error} title="Ошибка создания фильма" />}

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Основная информация</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="name">Название фильма *</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => updateFormData("name", e.target.value)}
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="x">Координата X *</Label>
                  <Input
                    id="x"
                    type="number"
                    value={formData.x}
                    onChange={(e) => updateFormData("x", Number.parseInt(e.target.value))}
                    required
                  />
                </div>
                <div>
                  <Label htmlFor="y">Координата Y * (макс. 558)</Label>
                  <Input
                    id="y"
                    type="number"
                    max={558}
                    value={formData.y}
                    onChange={(e) => updateFormData("y", Number.parseFloat(e.target.value))}
                    required
                  />
                </div>
              </div>

              <div>
                <Label htmlFor="genre">Жанр *</Label>
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
                  <Label htmlFor="oscarsCount">Количество Оскаров * (мин. 0)</Label>
                  <Input
                    id="oscarsCount"
                    type="number"
                    min={0}
                    value={formData.oscarsCount}
                    onChange={(e) => updateFormData("oscarsCount", Number.parseInt(e.target.value))}
                    required
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
                <Label htmlFor="screenwriterName">Имя сценариста *</Label>
                <Input
                  id="screenwriterName"
                  value={formData.screenwriterName}
                  onChange={(e) => updateFormData("screenwriterName", e.target.value)}
                  required
                />
              </div>

              <div>
                <Label htmlFor="screenwriterBirthday">Дата рождения *</Label>
                <Input
                  id="screenwriterBirthday"
                  type="date"
                  value={formData.screenwriterBirthday}
                  onChange={(e) => updateFormData("screenwriterBirthday", e.target.value)}
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="screenwriterHeight">Рост * (больше 0)</Label>
                  <Input
                    id="screenwriterHeight"
                    type="number"
                    min={0.01}
                    step={0.01}
                    value={formData.screenwriterHeight}
                    onChange={(e) => updateFormData("screenwriterHeight", Number.parseFloat(e.target.value))}
                    required
                  />
                </div>
                <div>
                  <Label htmlFor="screenwriterWeight">Вес * (мин. 1)</Label>
                  <Input
                    id="screenwriterWeight"
                    type="number"
                    min={1}
                    value={formData.screenwriterWeight}
                    onChange={(e) => updateFormData("screenwriterWeight", Number.parseInt(e.target.value))}
                    required
                  />
                </div>
              </div>

              <div>
                <Label htmlFor="screenwriterPassportID">ID паспорта *</Label>
                <Input
                  id="screenwriterPassportID"
                  value={formData.screenwriterPassportID}
                  onChange={(e) => updateFormData("screenwriterPassportID", e.target.value)}
                  required
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
                  Создание...
                </>
              ) : (
                "Создать фильм"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { Award, TrendingUp } from "lucide-react"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie } from "@/lib/api-client"
import { useToast } from "@/hooks/use-toast"
import { callbackService } from "@/lib/callback-service"

interface HonorMoviesCardProps {
  movies: Movie[]
  onMoviesUpdated: (movies: Movie[]) => void
}

export function HonorMoviesCard({ movies, onMoviesUpdated }: HonorMoviesCardProps) {
  const [lengthForm, setLengthForm] = useState({ minLength: "", oscarsToAdd: "" })
  const [fewOscarsForm, setFewOscarsForm] = useState({ maxOscars: "", oscarsToAdd: "" })
  const [loading, setLoading] = useState({ byLength: false, fewOscars: false })
  const [error, setError] = useState<unknown>(null)
  const { toast } = useToast()

  // Инициализируем callback service с toast
  callbackService.setToast(toast)

  const handleHonorByLength = async () => {
    if (!lengthForm.minLength || !lengthForm.oscarsToAdd) {
      toast({
        title: "Ошибка",
        description: "Заполните все поля",
        variant: "destructive",
      })
      return
    }

    try {
      setLoading((prev) => ({ ...prev, byLength: true }))
      setError(null)
      
      // Отправляем событие о начале операции
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('operation-start', {
          detail: { operationId: 'onAwarded' }
        }))
      }
      
      const callbackUrls = callbackService.getCallbackUrls()
      const result = await apiClient.honorMoviesByLength(
        Number.parseFloat(lengthForm.minLength),
        Number.parseInt(lengthForm.oscarsToAdd),
        callbackUrls.onAwarded,
      )

      // Update movies list with updated movies
      const updatedMovies = movies.map((movie) => {
        const updatedMovie = result.updatedMovies.find((um) => um.id === movie.id)
        return updatedMovie || movie
      })
      onMoviesUpdated(updatedMovies)

      toast({
        title: "Успех",
        description: `Обновлено ${result.updatedCount} фильмов`,
      })

      setLengthForm({ minLength: "", oscarsToAdd: "" })
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, byLength: false }))
    }
  }

  const handleHonorFewOscars = async () => {
    if (!fewOscarsForm.maxOscars || !fewOscarsForm.oscarsToAdd) {
      toast({
        title: "Ошибка",
        description: "Заполните все поля",
        variant: "destructive",
      })
      return
    }

    try {
      setLoading((prev) => ({ ...prev, fewOscars: true }))
      setError(null)
      
      // Отправляем событие о начале операции
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('operation-start', {
          detail: { operationId: 'notifyAdmins' }
        }))
      }
      
      const callbackUrls = callbackService.getCallbackUrls()
      const result = await apiClient.honorMoviesWithFewOscars(
        Number.parseInt(fewOscarsForm.maxOscars),
        Number.parseInt(fewOscarsForm.oscarsToAdd),
        callbackUrls.notifyAdmins,
      )

      // Update movies list with updated movies
      const updatedMovies = movies.map((movie) => {
        const updatedMovie = result.updatedMovies.find((um) => um.id === movie.id)
        return updatedMovie || movie
      })
      onMoviesUpdated(updatedMovies)

      toast({
        title: "Успех",
        description: `Обновлено ${result.updatedCount} фильмов`,
      })

      setFewOscarsForm({ maxOscars: "", oscarsToAdd: "" })
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, fewOscars: false }))
    }
  }

  return (
    <div className="grid gap-6 md:grid-cols-2">
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <TrendingUp className="h-5 w-5" />
            <CardTitle>Награждение по длительности</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {!!error && <ErrorDisplay error={error} title="Ошибка награждения" />}

          <div className="space-y-4">
            <div>
              <Label htmlFor="minLength">Минимальная длительность</Label>
              <Input
                id="minLength"
                type="number"
                min={0}
                step={0.1}
                placeholder="Например: 120.5"
                value={lengthForm.minLength}
                onChange={(e) => setLengthForm((prev) => ({ ...prev, minLength: e.target.value }))}
              />
            </div>

            <div>
              <Label htmlFor="oscarsToAddLength">Количество Оскаров для добавления</Label>
              <Input
                id="oscarsToAddLength"
                type="number"
                min={1}
                placeholder="Например: 2"
                value={lengthForm.oscarsToAdd}
                onChange={(e) => setLengthForm((prev) => ({ ...prev, oscarsToAdd: e.target.value }))}
              />
            </div>

            <Button onClick={handleHonorByLength} disabled={loading.byLength} className="w-full">
              {loading.byLength ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Награждение...
                </>
              ) : (
                <>
                  <Award className="mr-2 h-4 w-4" />
                  Наградить фильмы
                </>
              )}
            </Button>
          </div>

          <Separator />

          <div className="text-sm text-muted-foreground">
            <p>Награждает дополнительными Оскарами все фильмы, длительность которых превышает указанное значение.</p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Award className="h-5 w-5" />
            <CardTitle>Награждение с малым количеством Оскаров</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-4">
            <div>
              <Label htmlFor="maxOscars">Максимальное количество Оскаров</Label>
              <Input
                id="maxOscars"
                type="number"
                min={1}
                placeholder="Например: 3"
                value={fewOscarsForm.maxOscars}
                onChange={(e) => setFewOscarsForm((prev) => ({ ...prev, maxOscars: e.target.value }))}
              />
            </div>

            <div>
              <Label htmlFor="oscarsToAddFew">Количество Оскаров для добавления</Label>
              <Input
                id="oscarsToAddFew"
                type="number"
                min={1}
                placeholder="Например: 1"
                value={fewOscarsForm.oscarsToAdd}
                onChange={(e) => setFewOscarsForm((prev) => ({ ...prev, oscarsToAdd: e.target.value }))}
              />
            </div>

            <Button onClick={handleHonorFewOscars} disabled={loading.fewOscars} className="w-full">
              {loading.fewOscars ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Награждение...
                </>
              ) : (
                <>
                  <Award className="mr-2 h-4 w-4" />
                  Наградить фильмы
                </>
              )}
            </Button>
          </div>

          <Separator />

          <div className="text-sm text-muted-foreground">
            <p>
              Награждает дополнительными Оскарами все фильмы, у которых количество Оскаров меньше или равно указанному
              значению.
            </p>
          </div>
        </CardContent>
      </Card>

      {movies.length > 0 && (
        <Card className="md:col-span-2">
          <CardHeader>
            <CardTitle>Статистика фильмов</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid gap-4 md:grid-cols-4">
              <div className="text-center">
                <div className="text-2xl font-bold">{movies.length}</div>
                <div className="text-sm text-muted-foreground">Всего фильмов</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold">{movies.reduce((sum, movie) => sum + movie.oscarsCount, 0)}</div>
                <div className="text-sm text-muted-foreground">Всего Оскаров</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold">
                  {Math.round((movies.reduce((sum, movie) => sum + movie.oscarsCount, 0) / movies.length) * 10) / 10}
                </div>
                <div className="text-sm text-muted-foreground">Среднее Оскаров</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold">
                  {movies.reduce((sum, movie) => sum + (movie.goldenPalmCount || 0), 0)}
                </div>
                <div className="text-sm text-muted-foreground">Золотых пальм</div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}

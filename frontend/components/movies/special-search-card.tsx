"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Search, Calculator, Trash2 } from "lucide-react"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Movie } from "@/lib/api-client"
import { useToast } from "@/hooks/use-toast"

interface SpecialSearchCardProps {
  onSpecialSearch: (type: string, results: Movie[] | { count: number }, query: string) => void
}

export function SpecialSearchCard({ onSpecialSearch }: SpecialSearchCardProps) {
  const [prefixSearch, setPrefixSearch] = useState("")
  const [countSearch, setCountSearch] = useState("")
  const [deleteCount, setDeleteCount] = useState("")
  const [loading, setLoading] = useState({ prefix: false, count: false, delete: false })
  const [error, setError] = useState<unknown>(null)
  const { toast } = useToast()

  const handlePrefixSearch = async () => {
    if (!prefixSearch.trim()) {
      toast({
        title: "Ошибка",
        description: "Введите префикс для поиска",
        variant: "destructive",
      })
      return
    }

    try {
      setLoading((prev) => ({ ...prev, prefix: true }))
      setError(null)
      const results = await apiClient.getMoviesByNamePrefix(prefixSearch.trim())
      onSpecialSearch("Фильмы с префиксом", results, prefixSearch.trim())
      toast({
        title: "Поиск выполнен",
        description: `Найдено ${results.length} фильмов`,
      })
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, prefix: false }))
    }
  }

  const handleCountSearch = async () => {
    const count = Number.parseInt(countSearch)
    if (!countSearch || count < 1) {
      toast({
        title: "Ошибка",
        description: "Введите корректное количество Оскаров",
        variant: "destructive",
      })
      return
    }

    try {
      setLoading((prev) => ({ ...prev, count: true }))
      setError(null)
      const result = await apiClient.countMoviesWithOscarsLessThan(count)
      onSpecialSearch("Фильмов с Оскарами меньше", result, count.toString())
      toast({
        title: "Подсчет выполнен",
        description: `Найдено ${result.count} фильмов`,
      })
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, count: false }))
    }
  }

  const handleDeleteByOscars = async () => {
    const count = Number.parseInt(deleteCount)
    if (!deleteCount || count < 1) {
      toast({
        title: "Ошибка",
        description: "Введите корректное количество Оскаров",
        variant: "destructive",
      })
      return
    }

    try {
      setLoading((prev) => ({ ...prev, delete: true }))
      setError(null)
      await apiClient.deleteMoviesByOscarsCount(count)
      toast({
        title: "Удаление выполнено",
        description: `Удалены все фильмы с ${count} Оскарами`,
      })
      setDeleteCount("")
      // Trigger a refresh of the main movies list
      window.location.reload()
    } catch (err) {
      setError(err)
    } finally {
      setLoading((prev) => ({ ...prev, delete: false }))
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Специальные операции</CardTitle>
      </CardHeader>
      <CardContent>
        {!!error && <ErrorDisplay error={error} title="Ошибка операции" />}

        <Tabs defaultValue="prefix" className="space-y-4">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="prefix">Поиск по префиксу</TabsTrigger>
            <TabsTrigger value="count">Подсчет фильмов</TabsTrigger>
            <TabsTrigger value="delete">Удаление по Оскарам</TabsTrigger>
          </TabsList>

          <TabsContent value="prefix" className="space-y-4">
            <div className="flex gap-4">
              <div className="flex-1">
                <Label htmlFor="prefix-input">Префикс названия</Label>
                <Input
                  id="prefix-input"
                  placeholder="Например: Мст"
                  value={prefixSearch}
                  onChange={(e) => setPrefixSearch(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handlePrefixSearch()}
                />
              </div>
              <div className="flex items-end">
                <Button onClick={handlePrefixSearch} disabled={loading.prefix}>
                  {loading.prefix ? (
                    <>
                      <LoadingSpinner size="sm" className="mr-2" />
                      Поиск...
                    </>
                  ) : (
                    <>
                      <Search className="mr-2 h-4 w-4" />
                      Найти фильмы
                    </>
                  )}
                </Button>
              </div>
            </div>
            <p className="text-sm text-muted-foreground">
              Найти все фильмы, название которых начинается с указанной подстроки
            </p>
          </TabsContent>

          <TabsContent value="count" className="space-y-4">
            <div className="flex gap-4">
              <div className="flex-1">
                <Label htmlFor="count-input">Количество Оскаров</Label>
                <Input
                  id="count-input"
                  type="number"
                  min={1}
                  placeholder="Например: 5"
                  value={countSearch}
                  onChange={(e) => setCountSearch(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleCountSearch()}
                />
              </div>
              <div className="flex items-end">
                <Button onClick={handleCountSearch} disabled={loading.count}>
                  {loading.count ? (
                    <>
                      <LoadingSpinner size="sm" className="mr-2" />
                      Подсчет...
                    </>
                  ) : (
                    <>
                      <Calculator className="mr-2 h-4 w-4" />
                      Подсчитать
                    </>
                  )}
                </Button>
              </div>
            </div>
            <p className="text-sm text-muted-foreground">
              Подсчитать количество фильмов с количеством Оскаров меньше указанного значения
            </p>
          </TabsContent>

          <TabsContent value="delete" className="space-y-4">
            <div className="flex gap-4">
              <div className="flex-1">
                <Label htmlFor="delete-input">Количество Оскаров для удаления</Label>
                <Input
                  id="delete-input"
                  type="number"
                  min={1}
                  placeholder="Например: 1"
                  value={deleteCount}
                  onChange={(e) => setDeleteCount(e.target.value)}
                />
              </div>
              <div className="flex items-end">
                <Button onClick={handleDeleteByOscars} disabled={loading.delete} variant="destructive">
                  {loading.delete ? (
                    <>
                      <LoadingSpinner size="sm" className="mr-2" />
                      Удаление...
                    </>
                  ) : (
                    <>
                      <Trash2 className="mr-2 h-4 w-4" />
                      Удалить фильмы
                    </>
                  )}
                </Button>
              </div>
            </div>
            <p className="text-sm text-muted-foreground text-destructive">
              ⚠️ Удалить все фильмы с указанным количеством Оскаров. Это действие нельзя отменить!
            </p>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}

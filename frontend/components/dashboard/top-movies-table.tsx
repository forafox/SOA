"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Trophy, DollarSign, Star } from "lucide-react"
import type { Movie } from "@/lib/api-client"

interface TopMoviesTableProps {
  movies: Movie[]
}

export function TopMoviesTable({ movies }: TopMoviesTableProps) {
  const [showCount, setShowCount] = useState(10)

  const genreLabels = {
    ACTION: "Боевик",
    ADVENTURE: "Приключения",
    TRAGEDY: "Трагедия",
    FANTASY: "Фэнтези",
  }

  const formatCurrency = (amount?: number) => {
    if (!amount) return "Не указан"
    return new Intl.NumberFormat("ru-RU", {
      style: "currency",
      currency: "USD",
      notation: "compact",
      maximumFractionDigits: 1,
    }).format(amount)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ru-RU")
  }

  // Top movies by Oscars
  const topByOscars = [...movies].sort((a, b) => b.oscarsCount - a.oscarsCount).slice(0, showCount)

  // Top movies by budget
  const topByBudget = [...movies]
    .filter((movie) => movie.budget && movie.budget > 0)
    .sort((a, b) => (b.budget || 0) - (a.budget || 0))
    .slice(0, showCount)

  // Top movies by Golden Palms
  const topByGoldenPalms = [...movies]
    .filter((movie) => movie.goldenPalmCount && movie.goldenPalmCount > 0)
    .sort((a, b) => (b.goldenPalmCount || 0) - (a.goldenPalmCount || 0))
    .slice(0, showCount)

  const TableComponent = ({ movies, type }: { movies: Movie[]; type: "oscars" | "budget" | "palms" }) => (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-12">#</TableHead>
              <TableHead>Название</TableHead>
              <TableHead>Жанр</TableHead>
              {type === "oscars" && <TableHead>Оскары</TableHead>}
              {type === "budget" && <TableHead>Бюджет</TableHead>}
              {type === "palms" && <TableHead>Золотые пальмы</TableHead>}
              <TableHead>Сценарист</TableHead>
              <TableHead>Дата создания</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {movies.map((movie, index) => (
              <TableRow key={movie.id}>
                <TableCell className="font-medium">
                  <div className="flex items-center gap-2">
                    {index < 3 && <Trophy className="h-4 w-4 text-yellow-500" />}
                    {index + 1}
                  </div>
                </TableCell>
                <TableCell className="font-medium">{movie.name}</TableCell>
                <TableCell>
                  <Badge variant="secondary">{genreLabels[movie.genre]}</Badge>
                </TableCell>
                {type === "oscars" && <TableCell className="font-bold">{movie.oscarsCount}</TableCell>}
                {type === "budget" && <TableCell className="font-bold">{formatCurrency(movie.budget)}</TableCell>}
                {type === "palms" && <TableCell className="font-bold">{movie.goldenPalmCount}</TableCell>}
                <TableCell>{movie.screenwriter.name}</TableCell>
                <TableCell>{formatDate(movie.creationDate)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {movies.length === 0 && <div className="text-center py-8 text-muted-foreground">Нет данных для отображения</div>}

      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          Показано {Math.min(showCount, movies.length)} из {movies.length}
        </p>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={() => setShowCount(10)} disabled={showCount === 10}>
            Топ 10
          </Button>
          <Button variant="outline" size="sm" onClick={() => setShowCount(25)} disabled={showCount === 25}>
            Топ 25
          </Button>
          <Button variant="outline" size="sm" onClick={() => setShowCount(movies.length)}>
            Показать все
          </Button>
        </div>
      </div>
    </div>
  )

  return (
    <Card>
      <CardHeader>
        <CardTitle>Топ фильмы</CardTitle>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="oscars" className="space-y-4">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="oscars" className="flex items-center gap-2">
              <Trophy className="h-4 w-4" />
              По Оскарам
            </TabsTrigger>
            <TabsTrigger value="budget" className="flex items-center gap-2">
              <DollarSign className="h-4 w-4" />
              По бюджету
            </TabsTrigger>
            <TabsTrigger value="palms" className="flex items-center gap-2">
              <Star className="h-4 w-4" />
              По Золотым пальмам
            </TabsTrigger>
          </TabsList>

          <TabsContent value="oscars">
            <TableComponent movies={topByOscars} type="oscars" />
          </TabsContent>

          <TabsContent value="budget">
            <TableComponent movies={topByBudget} type="budget" />
          </TabsContent>

          <TabsContent value="palms">
            <TableComponent movies={topByGoldenPalms} type="palms" />
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}

"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Users, RefreshCw } from "lucide-react"
import { ErrorDisplay } from "@/components/error-display"
import { LoadingSpinner } from "@/components/loading-spinner"
import { apiClient, type Person } from "@/lib/api-client"

export function OscarLosersCard() {
  const [losers, setLosers] = useState<Person[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<unknown>(null)

  const loadOscarLosers = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await apiClient.getOscarLosers()
      setLosers(data || [])
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ru-RU")
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Users className="h-5 w-5" />
            <CardTitle>Операторы без Оскаров</CardTitle>
          </div>
          <Button onClick={loadOscarLosers} disabled={loading} variant="outline" size="sm">
            <RefreshCw className={`h-4 w-4 mr-2 ${loading ? "animate-spin" : ""}`} />
            Обновить
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        {!!error && <ErrorDisplay error={error} title="Ошибка загрузки операторов" />}

        {loading ? (
          <div className="flex justify-center py-8">
            <LoadingSpinner size="lg" />
          </div>
        ) : losers.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            {error ? "Нажмите 'Обновить' для загрузки данных" : "Все операторы имеют Оскары!"}
          </div>
        ) : (
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <Badge variant="secondary">{losers.length} операторов без наград</Badge>
            </div>

            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Имя</TableHead>
                    <TableHead>Дата рождения</TableHead>
                    <TableHead>Рост</TableHead>
                    <TableHead>Вес</TableHead>
                    <TableHead>ID паспорта</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {losers.map((person, index) => (
                    <TableRow key={`${person.passportID}-${index}`}>
                      <TableCell className="font-medium">{person.name}</TableCell>
                      <TableCell>{formatDate(person.birthday)}</TableCell>
                      <TableCell>{person.height} см</TableCell>
                      <TableCell>{person.weight} кг</TableCell>
                      <TableCell className="font-mono text-sm">{person.passportID}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

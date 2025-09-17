"use client"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { X, Filter, RotateCcw } from "lucide-react"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { useState } from "react"
import type { MovieFilters, PaginationParams } from "@/lib/api-client"

interface AdvancedFiltersProps {
  filters: MovieFilters
  onFiltersChange: (filters: MovieFilters) => void
  pagination: PaginationParams
  onPaginationChange: (pagination: PaginationParams) => void
  onClearFilters: () => void
}

export function AdvancedFilters({
  filters,
  onFiltersChange,
  pagination,
  onPaginationChange,
  onClearFilters,
}: AdvancedFiltersProps) {
  const [isOpen, setIsOpen] = useState(false)

  const genreOptions = [
    { value: "ACTION", label: "Боевик" },
    { value: "ADVENTURE", label: "Приключения" },
    { value: "TRAGEDY", label: "Трагедия" },
    { value: "FANTASY", label: "Фэнтези" },
  ]

  const sortOptions = [
    { value: "name", label: "По названию" },
    { value: "oscarsCount", label: "По количеству Оскаров" },
    { value: "creationDate", label: "По дате создания" },
    { value: "budget", label: "По бюджету" },
    { value: "genre", label: "По жанру" },
  ]

  const pageSizeOptions = [10, 20, 50, 100]

  const updateFilters = (key: keyof MovieFilters, value: string | undefined) => {
    onFiltersChange({ ...filters, [key]: value || undefined })
  }

  const updatePagination = (key: keyof PaginationParams, value: number) => {
    onPaginationChange({ ...pagination, [key]: value })
  }

  const getActiveFiltersCount = () => {
    return Object.values(filters).filter(Boolean).length
  }

  const clearFilter = (key: keyof MovieFilters) => {
    const newFilters = { ...filters }
    delete newFilters[key]
    onFiltersChange(newFilters)
  }

  return (
    <Card>
      <Collapsible open={isOpen} onOpenChange={setIsOpen}>
        <CollapsibleTrigger asChild>
          <CardHeader className="cursor-pointer hover:bg-muted/50 transition-colors">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Filter className="h-5 w-5" />
                <CardTitle>Расширенные фильтры</CardTitle>
                {getActiveFiltersCount() > 0 && <Badge variant="secondary">{getActiveFiltersCount()} активных</Badge>}
              </div>
              <Button variant="ghost" size="sm">
                {isOpen ? "Скрыть" : "Показать"}
              </Button>
            </div>
          </CardHeader>
        </CollapsibleTrigger>

        <CollapsibleContent>
          <CardContent className="space-y-6">
            {/* Active Filters Display */}
            {getActiveFiltersCount() > 0 && (
              <div className="space-y-2">
                <Label>Активные фильтры:</Label>
                <div className="flex flex-wrap gap-2">
                  {filters.name && (
                    <Badge variant="outline" className="gap-1">
                      Название: {filters.name}
                      <X className="h-3 w-3 cursor-pointer" onClick={() => clearFilter("name")} />
                    </Badge>
                  )}
                  {filters.genre && (
                    <Badge variant="outline" className="gap-1">
                      Жанр: {genreOptions.find((g) => g.value === filters.genre)?.label}
                      <X className="h-3 w-3 cursor-pointer" onClick={() => clearFilter("genre")} />
                    </Badge>
                  )}
                  {filters.sort && (
                    <Badge variant="outline" className="gap-1">
                      Сортировка: {sortOptions.find((s) => s.value === filters.sort)?.label}
                      <X className="h-3 w-3 cursor-pointer" onClick={() => clearFilter("sort")} />
                    </Badge>
                  )}
                </div>
              </div>
            )}

            {/* Filter Controls */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              <div>
                <Label htmlFor="genre-filter">Жанр</Label>
                <Select value={filters.genre || "ALL"} onValueChange={(value) => updateFilters("genre", value)}>
                  <SelectTrigger>
                    <SelectValue placeholder="Все жанры" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">Все жанры</SelectItem>
                    {genreOptions.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label htmlFor="sort-filter">Сортировка</Label>
                <Select value={filters.sort || "NONE"} onValueChange={(value) => updateFilters("sort", value)}>
                  <SelectTrigger>
                    <SelectValue placeholder="Без сортировки" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="NONE">Без сортировки</SelectItem>
                    {sortOptions.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label htmlFor="page-size">Элементов на странице</Label>
                <Select
                  value={pagination.size?.toString() || "20"}
                  onValueChange={(value) => updatePagination("size", Number.parseInt(value))}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {pageSizeOptions.map((size) => (
                      <SelectItem key={size} value={size.toString()}>
                        {size}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Pagination Controls */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Label htmlFor="page-input">Страница:</Label>
                <Input
                  id="page-input"
                  type="number"
                  min={1}
                  value={pagination.page || 1}
                  onChange={(e) => updatePagination("page", Number.parseInt(e.target.value) || 1)}
                  className="w-20"
                />
              </div>

              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => updatePagination("page", Math.max(1, (pagination.page || 1) - 1))}
                  disabled={(pagination.page || 1) <= 1}
                >
                  Предыдущая
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => updatePagination("page", (pagination.page || 1) + 1)}
                >
                  Следующая
                </Button>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={onClearFilters}>
                <RotateCcw className="mr-2 h-4 w-4" />
                Сбросить все
              </Button>
            </div>
          </CardContent>
        </CollapsibleContent>
      </Collapsible>
    </Card>
  )
}

export interface Movie {
  id: number
  name: string
  coordinates: {
    x: number
    y: number
  }
  creationDate: string
  oscarsCount: number
  goldenPalmCount?: number
  budget?: number
  genre: "ACTION" | "ADVENTURE" | "TRAGEDY" | "FANTASY"
  screenwriter: {
    name: string
    birthday: string
    height: number
    weight: number
    passportID: string
  }
}

export interface CreateMovieData {
  name: string
  x: number
  y: number
  oscarsCount: number
  goldenPalmCount?: number
  budget?: number
  genre: "ACTION" | "ADVENTURE" | "TRAGEDY" | "FANTASY"
  screenwriterName: string
  screenwriterBirthday: string
  screenwriterHeight: number
  screenwriterWeight: number
  screenwriterPassportID: string
}

export class ApiError {
  message: string
  status?: number

  constructor(error: { message: string; status?: number }) {
    this.message = error.message
    this.status = error.status
  }
}

export interface PaginationParams {
  page?: number
  size?: number
}

export interface MovieFilters {
  name?: string
  genre?: "ACTION" | "ADVENTURE" | "TRAGEDY" | "FANTASY"
  sort?: string
}

export interface OscarAward {
  awardId: number
  date: string
  category: string
}

export interface Person {
  name: string
  birthday: string
  height: number
  weight: number
  passportID: string
}

import {
  mockMovies,
  mockOscarLosers,
  mockOscarAwards,
  getMockMovieById,
  filterMockMovies,
  paginateMockData,
} from "./mock-data"

// Configuration for using mock data vs real API
function getUseMockData(): boolean {
  if (typeof window === "undefined") return false
  return localStorage.getItem("useMockData") === "true"
}

class MoviesOscarsApiClient {
  private baseUrl: string

  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      let errorMessage = `HTTP ${response.status}: ${response.statusText}`

      try {
        const errorData = await response.json()
        if (errorData.message) {
          errorMessage = errorData.message
        }
      } catch {
        // If JSON parsing fails, use the default error message
      }

      throw new ApiError({
        message: errorMessage,
        status: response.status,
      })
    }

    // Handle 204 No Content responses
    if (response.status === 204) {
      return null as T
    }

    try {
      return await response.json()
    } catch {
      return null as T
    }
  }

  // Movies API methods
  async getMovies(filters: MovieFilters = {}, pagination: PaginationParams = {}): Promise<Movie[]> {
    if (getUseMockData()) {
      const filtered = filterMockMovies(filters)
      return paginateMockData(filtered, pagination.page, pagination.size)
    }

    const params = new URLSearchParams()

    if (filters.name) params.append("name", filters.name)
    if (filters.genre) params.append("genre", filters.genre)
    if (filters.sort) params.append("sort", filters.sort)
    if (pagination.page) params.append("page", pagination.page.toString())
    if (pagination.size) params.append("size", pagination.size.toString())

    const response = await fetch(`${this.baseUrl}/movies?${params}`)
    return this.handleResponse<Movie[]>(response)
  }

  async getMovieById(id: number): Promise<Movie | null> {
    if (getUseMockData()) {
      return getMockMovieById(id) || null
    }

    const response = await fetch(`${this.baseUrl}/movies/${id}`)
    return this.handleResponse<Movie>(response)
  }

  async createMovie(data: CreateMovieData): Promise<Movie> {
    if (getUseMockData()) {
      const newMovie: Movie = {
        id: Math.max(...mockMovies.map((m) => m.id)) + 1,
        name: data.name,
        coordinates: { x: data.x, y: data.y },
        creationDate: new Date().toISOString(),
        oscarsCount: data.oscarsCount,
        goldenPalmCount: data.goldenPalmCount,
        budget: data.budget,
        genre: data.genre,
        screenwriter: {
          name: data.screenwriterName,
          birthday: data.screenwriterBirthday,
          height: data.screenwriterHeight,
          weight: data.screenwriterWeight,
          passportID: data.screenwriterPassportID,
        },
      }
      mockMovies.push(newMovie)
      return newMovie
    }

    const params = new URLSearchParams()
    params.append("name", data.name)
    params.append("x", data.x.toString())
    params.append("y", data.y.toString())
    params.append("oscarsCount", data.oscarsCount.toString())
    if (data.goldenPalmCount) params.append("goldenPalmCount", data.goldenPalmCount.toString())
    if (data.budget) params.append("budget", data.budget.toString())
    params.append("genre", data.genre)
    params.append("screenwriterName", data.screenwriterName)
    params.append("screenwriterBirthday", data.screenwriterBirthday)
    params.append("screenwriterHeight", data.screenwriterHeight.toString())
    params.append("screenwriterWeight", data.screenwriterWeight.toString())
    params.append("screenwriterPassportID", data.screenwriterPassportID)

    const response = await fetch(`${this.baseUrl}/movies?${params}`, {
      method: "POST",
    })
    return this.handleResponse<Movie>(response)
  }

  async updateMovie(id: number, data: Partial<Movie>): Promise<Movie | null> {
    if (getUseMockData()) {
      const movieIndex = mockMovies.findIndex((m) => m.id === id)
      if (movieIndex === -1) return null

      mockMovies[movieIndex] = { ...mockMovies[movieIndex], ...data }
      return mockMovies[movieIndex]
    }

    const response = await fetch(`${this.baseUrl}/movies/${id}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
    return this.handleResponse<Movie>(response)
  }

  async deleteMovie(id: number): Promise<void> {
    if (getUseMockData()) {
      const movieIndex = mockMovies.findIndex((m) => m.id === id)
      if (movieIndex !== -1) {
        mockMovies.splice(movieIndex, 1)
      }
      return
    }

    const response = await fetch(`${this.baseUrl}/movies/${id}`, {
      method: "DELETE",
    })
    await this.handleResponse<void>(response)
  }

  async deleteMoviesByOscarsCount(count: number): Promise<void> {
    if (getUseMockData()) {
      for (let i = mockMovies.length - 1; i >= 0; i--) {
        if (mockMovies[i].oscarsCount === count) {
          mockMovies.splice(i, 1)
        }
      }
      return
    }

    const response = await fetch(`${this.baseUrl}/movies/oscarsCount/${count}`, {
      method: "DELETE",
    })
    await this.handleResponse<void>(response)
  }

  async countMoviesWithOscarsLessThan(count: number): Promise<{ count: number }> {
    if (getUseMockData()) {
      const filteredCount = mockMovies.filter((movie) => movie.oscarsCount < count).length
      return { count: filteredCount }
    }

    const response = await fetch(`${this.baseUrl}/movies/count/oscars-less-than/${count}`)
    return this.handleResponse<{ count: number }>(response)
  }

  async getMoviesByNamePrefix(prefix: string): Promise<Movie[]> {
    if (getUseMockData()) {
      return mockMovies.filter((movie) => movie.name.toLowerCase().startsWith(prefix.toLowerCase()))
    }

    const response = await fetch(`${this.baseUrl}/movies/name-starts-with/${encodeURIComponent(prefix)}`)
    return this.handleResponse<Movie[]>(response)
  }

  // Oscars API methods
  async getOscarLosers(): Promise<Person[]> {
    if (getUseMockData()) {
      return [...mockOscarLosers]
    }

    const response = await fetch(`${this.baseUrl}/oscars/operators/losers`)
    return this.handleResponse<Person[]>(response)
  }

  async honorMoviesByLength(
    minLength: number,
    oscarsToAdd: number,
  ): Promise<{
    updatedCount: number
    updatedMovies: Movie[]
  }> {
    if (getUseMockData()) {
      // Mock implementation: honor movies with name length >= minLength
      const moviesToUpdate = mockMovies.filter((movie) => movie.name.length >= minLength)
      moviesToUpdate.forEach((movie) => {
        movie.oscarsCount += oscarsToAdd
      })

      return {
        updatedCount: moviesToUpdate.length,
        updatedMovies: moviesToUpdate,
      }
    }

    const params = new URLSearchParams()
    params.append("oscarsToAdd", oscarsToAdd.toString())

    const response = await fetch(`${this.baseUrl}/oscars/movies/honor-by-length/${minLength}?${params}`, {
      method: "POST",
    })
    return this.handleResponse<{ updatedCount: number; updatedMovies: Movie[] }>(response)
  }

  async honorMoviesWithFewOscars(
    maxOscars: number,
    oscarsToAdd: number,
  ): Promise<{
    updatedCount: number
    updatedMovies: Movie[]
  }> {
    if (getUseMockData()) {
      const moviesToUpdate = mockMovies.filter((movie) => movie.oscarsCount <= maxOscars)
      moviesToUpdate.forEach((movie) => {
        movie.oscarsCount += oscarsToAdd
      })

      return {
        updatedCount: moviesToUpdate.length,
        updatedMovies: moviesToUpdate,
      }
    }

    const params = new URLSearchParams()
    params.append("maxOscars", maxOscars.toString())
    params.append("oscarsToAdd", oscarsToAdd.toString())

    const response = await fetch(`${this.baseUrl}/oscars/movies/honor-low-oscars?${params}`, {
      method: "POST",
    })
    return this.handleResponse<{ updatedCount: number; updatedMovies: Movie[] }>(response)
  }

  async getOscarsByMovie(movieId: number, pagination: PaginationParams = {}): Promise<OscarAward[]> {
    if (getUseMockData()) {
      const awards = mockOscarAwards[movieId] || []
      return paginateMockData(awards, pagination.page, pagination.size)
    }

    const params = new URLSearchParams()
    if (pagination.page) params.append("page", pagination.page.toString())
    if (pagination.size) params.append("size", pagination.size.toString())

    const response = await fetch(`${this.baseUrl}/oscars/movies/${movieId}?${params}`)
    return this.handleResponse<OscarAward[]>(response)
  }

  async addOscarsToMovie(
    movieId: number,
    oscarsToAdd: number,
  ): Promise<{
    updatedCount: number
    updatedMovies: Movie[]
  }> {
    if (getUseMockData()) {
      const movie = mockMovies.find((m) => m.id === movieId)
      if (movie) {
        movie.oscarsCount += oscarsToAdd
        return {
          updatedCount: 1,
          updatedMovies: [movie],
        }
      }
      return { updatedCount: 0, updatedMovies: [] }
    }

    const params = new URLSearchParams()
    params.append("oscarsToAdd", oscarsToAdd.toString())

    const response = await fetch(`${this.baseUrl}/oscars/movies/${movieId}?${params}`, {
      method: "POST",
    })
    return this.handleResponse<{ updatedCount: number; updatedMovies: Movie[] }>(response)
  }

  async deleteOscarsByMovie(movieId: number): Promise<void> {
    if (getUseMockData()) {
      delete mockOscarAwards[movieId]
      const movie = mockMovies.find((m) => m.id === movieId)
      if (movie) {
        movie.oscarsCount = 0
      }
      return
    }

    const response = await fetch(`${this.baseUrl}/oscars/movies/${movieId}`, {
      method: "DELETE",
    })
    await this.handleResponse<void>(response)
  }
}

export const apiClient = new MoviesOscarsApiClient()
export { MoviesOscarsApiClient }

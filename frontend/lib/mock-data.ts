import type { Movie, Person, OscarAward } from "./api-client"

// Mock movies data
export const mockMovies: Movie[] = [
  {
    id: 1,
    name: "Титаник",
    coordinates: { x: 10, y: 20 },
    creationDate: "2023-01-15T10:30:00Z",
    oscarsCount: 11,
    goldenPalmCount: 0,
    budget: 200000000,
    genre: "TRAGEDY",
    screenwriter: {
      name: "Джеймс Кэмерон",
      birthday: "1954-08-16",
      height: 185,
      weight: 80,
      passportID: "US123456789",
    },
  },
  {
    id: 2,
    name: "Мстители: Финал",
    coordinates: { x: 50, y: 75 },
    creationDate: "2023-02-20T14:15:00Z",
    oscarsCount: 0,
    goldenPalmCount: 0,
    budget: 356000000,
    genre: "ACTION",
    screenwriter: {
      name: "Кристофер Маркус",
      birthday: "1970-01-02",
      height: 175,
      weight: 75,
      passportID: "US987654321",
    },
  },
  {
    id: 3,
    name: "Властелин колец: Возвращение короля",
    coordinates: { x: 30, y: 40 },
    creationDate: "2023-03-10T09:45:00Z",
    oscarsCount: 11,
    goldenPalmCount: 0,
    budget: 94000000,
    genre: "FANTASY",
    screenwriter: {
      name: "Питер Джексон",
      birthday: "1961-10-31",
      height: 169,
      weight: 85,
      passportID: "NZ456789123",
    },
  },
  {
    id: 4,
    name: "Индиана Джонс: В поисках утраченного ковчега",
    coordinates: { x: 80, y: 60 },
    creationDate: "2023-04-05T16:20:00Z",
    oscarsCount: 5,
    goldenPalmCount: 0,
    budget: 20000000,
    genre: "ADVENTURE",
    screenwriter: {
      name: "Лоуренс Кэздан",
      birthday: "1949-01-13",
      height: 180,
      weight: 78,
      passportID: "US111222333",
    },
  },
  {
    id: 5,
    name: "Темный рыцарь",
    coordinates: { x: 25, y: 85 },
    creationDate: "2023-05-12T11:30:00Z",
    oscarsCount: 2,
    goldenPalmCount: 0,
    budget: 185000000,
    genre: "ACTION",
    screenwriter: {
      name: "Кристофер Нолан",
      birthday: "1970-07-30",
      height: 181,
      weight: 82,
      passportID: "UK444555666",
    },
  },
]

// Mock Oscar losers (operators without Oscars)
export const mockOscarLosers: Person[] = [
  {
    name: "Алексей Петров",
    birthday: "1985-03-15",
    height: 175,
    weight: 70,
    passportID: "RU123456789",
  },
  {
    name: "Мария Иванова",
    birthday: "1990-07-22",
    height: 165,
    weight: 55,
    passportID: "RU987654321",
  },
  {
    name: "Дмитрий Сидоров",
    birthday: "1982-11-08",
    height: 180,
    weight: 85,
    passportID: "RU456789123",
  },
]

// Mock Oscar awards
export const mockOscarAwards: { [movieId: number]: OscarAward[] } = {
  1: [
    { awardId: 1, date: "1998-03-23", category: "Лучший фильм" },
    { awardId: 2, date: "1998-03-23", category: "Лучший режиссер" },
    { awardId: 3, date: "1998-03-23", category: "Лучшая операторская работа" },
  ],
  3: [
    { awardId: 4, date: "2004-02-29", category: "Лучший фильм" },
    { awardId: 5, date: "2004-02-29", category: "Лучший режиссер" },
    { awardId: 6, date: "2004-02-29", category: "Лучший адаптированный сценарий" },
  ],
  5: [
    { awardId: 7, date: "2009-02-22", category: "Лучший актер второго плана" },
    { awardId: 8, date: "2009-02-22", category: "Лучший звук" },
  ],
}

// Helper functions for mock data manipulation
export const getMockMovieById = (id: number): Movie | undefined => {
  return mockMovies.find((movie) => movie.id === id)
}

export const filterMockMovies = (filters: {
  name?: string
  genre?: string
  sort?: string
}): Movie[] => {
  let filtered = [...mockMovies]

  if (filters.name) {
    filtered = filtered.filter((movie) => movie.name.toLowerCase().includes(filters.name!.toLowerCase()))
  }

  if (filters.genre) {
    filtered = filtered.filter((movie) => movie.genre === filters.genre)
  }

  if (filters.sort) {
    const [field, direction] = filters.sort.split(",")
    filtered.sort((a, b) => {
      let aValue: string | number | Date
      let bValue: string | number | Date

      switch (field) {
        case "name":
          aValue = a.name
          bValue = b.name
          break
        case "oscarsCount":
          aValue = a.oscarsCount
          bValue = b.oscarsCount
          break
        case "budget":
          aValue = a.budget || 0
          bValue = b.budget || 0
          break
        case "creationDate":
          aValue = new Date(a.creationDate)
          bValue = new Date(b.creationDate)
          break
        default:
          return 0
      }

      if (direction === "desc") {
        return aValue > bValue ? -1 : aValue < bValue ? 1 : 0
      } else {
        return aValue < bValue ? -1 : aValue > bValue ? 1 : 0
      }
    })
  }

  return filtered
}

export const paginateMockData = <T,>(data: T[], page = 1, size = 10): T[] => {
  const startIndex = (page - 1) * size
  return data.slice(startIndex, startIndex + size)
}

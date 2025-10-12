"use client"

import { useState, useEffect } from "react"
import { Sidebar } from "@/components/sidebar"
import { MoviesPage } from "@/components/movies/movies-page"
import { OscarsPage } from "@/components/oscars/oscars-page"
import { DashboardPage } from "@/components/dashboard/dashboard-page"
import { AuthComponent } from "@/components/auth-component"
import oauth2AuthService from "@/lib/oauth2-auth"

type ActivePage = "movies" | "oscars" | "dashboard"

export default function HomePage() {
  const [activePage, setActivePage] = useState<ActivePage>("movies")
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Проверяем аутентификацию при загрузке
    const checkAuth = () => {
      try {
        const authenticated = oauth2AuthService.isUserAuthenticated()
        setIsAuthenticated(authenticated)
        console.log('Authentication status:', authenticated)
        if (authenticated) {
          const user = oauth2AuthService.getCurrentUser()
          console.log('Authenticated user:', user)
        }
      } catch (error) {
        console.error('Error checking authentication:', error)
        setIsAuthenticated(false)
      } finally {
        setIsLoading(false)
      }
    }
    checkAuth()
  }, [])

  const renderActivePage = () => {
    switch (activePage) {
      case "movies":
        return <MoviesPage />
      case "oscars":
        return <OscarsPage />
      case "dashboard":
        return <DashboardPage />
      default:
        return <MoviesPage />
    }
  }

  // Показываем загрузку пока проверяем аутентификацию
  if (isLoading) {
    return (
      <div className="flex h-screen bg-background items-center justify-center">
        <div className="w-full max-w-md">
          <div className="flex items-center justify-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
          <p className="text-center mt-4">Checking authentication...</p>
        </div>
      </div>
    )
  }

  // Если пользователь не аутентифицирован, показываем форму входа
  if (!isAuthenticated) {
    return (
      <div className="flex h-screen bg-background items-center justify-center">
        <div className="w-full max-w-md">
          <AuthComponent />
        </div>
      </div>
    )
  }

  return (
    <div className="flex h-screen bg-background">
      <Sidebar activePage={activePage} onPageChange={setActivePage} />
      <main className="flex-1 overflow-hidden">
        {renderActivePage()}
      </main>
    </div>
  )
}

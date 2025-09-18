"use client"

import { useState } from "react"
import { Sidebar } from "@/components/sidebar"
import { MoviesPage } from "@/components/movies/movies-page"
import { OscarsPage } from "@/components/oscars/oscars-page"
import { DashboardPage } from "@/components/dashboard/dashboard-page"

type ActivePage = "movies" | "oscars" | "dashboard"

export default function HomePage() {
  const [activePage, setActivePage] = useState<ActivePage>("movies")

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

  return (
    <div className="flex h-screen bg-background">
      <Sidebar activePage={activePage} onPageChange={setActivePage} />
      <main className="flex-1 overflow-hidden">
        {renderActivePage()}
      </main>
    </div>
  )
}

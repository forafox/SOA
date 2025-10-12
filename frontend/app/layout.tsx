import type React from "react"
import type { Metadata } from "next"
import { GeistSans } from "geist/font/sans"
import { GeistMono } from "geist/font/mono"
// import { Analytics } from "@vercel/analytics/next"
import { Toaster } from "@/components/ui/toaster"
import { GlobalCallbackNotifications } from "@/components/global-callback-notifications"
import { SmartCallbackMonitor } from "@/components/smart-callback-monitor"
import { Suspense } from "react"
import "./globals.css"

export const metadata: Metadata = {
  title: "Movies & Oscars Management",
  description: "Управление коллекцией фильмов и наград Оскар",
  generator: "v0.app",
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="ru">
      <body className={`font-sans ${GeistSans.variable} ${GeistMono.variable}`}>
        <Suspense fallback={null}>
          {children}
          <Toaster />
          <GlobalCallbackNotifications />
          <SmartCallbackMonitor />
          {/* <Analytics /> */}
        </Suspense>
      </body>
    </html>
  )
}

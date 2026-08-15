import { useEffect, useState } from "react"
import { AuthProvider, useAuth } from "./context/AuthContext"
import { DataProvider } from "./context/DataContext"
import { initTelegram, useBackButton } from "./lib/telegram"
import { BottomNav, type View } from "./components/BottomNav"
import { AuthPage } from "./pages/AuthPage"
import { TaskListPage } from "./pages/TaskListPage"
import { CreateTaskPage } from "./pages/CreateTaskPage"
import { SettingsPage } from "./pages/SettingsPage"

function AppShell() {
  const [view, setView] = useState<View>("tasks")

  // Telegram Back button returns to the task list from sub-views.
  useEffect(() => {
    return useBackButton(view !== "tasks", () => setView("tasks"))
  }, [view])

  return (
    <div className="mx-auto flex min-h-full max-w-md flex-col">
      <main className="flex-1">
        {view === "tasks" && <TaskListPage onCreate={() => setView("create")} />}
        {view === "create" && <CreateTaskPage onDone={() => setView("tasks")} />}
        {view === "settings" && <SettingsPage />}
      </main>
      <BottomNav current={view} onChange={setView} />
    </div>
  )
}

function Gate() {
  const { isAuthenticated, ready } = useAuth()

  if (!ready) {
    return (
      <div className="flex min-h-full items-center justify-center">
        <span className="h-6 w-6 animate-spin rounded-full border-2 border-muted border-t-transparent" />
      </div>
    )
  }

  if (!isAuthenticated) return <AuthPage />

  return (
    <DataProvider>
      <AppShell />
    </DataProvider>
  )
}

export default function App() {
  useEffect(() => {
    initTelegram()
  }, [])

  return (
    <AuthProvider>
      <Gate />
    </AuthProvider>
  )
}

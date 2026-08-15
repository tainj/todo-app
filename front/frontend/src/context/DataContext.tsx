import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react"
import { api } from "../lib/api"
import type { Category, NewTask, Task } from "../lib/types"

interface DataContextValue {
  tasks: Task[]
  categories: Category[]
  loading: boolean
  error: string | null
  refresh: () => Promise<void>
  createTask: (task: NewTask) => Promise<void>
  completeTask: (id: number) => Promise<void>
  deleteTask: (id: number) => Promise<void>
  createCategory: (name: string) => Promise<Category>
  deleteCategory: (id: number) => Promise<void>
}

const DataContext = createContext<DataContextValue | null>(null)

export function DataProvider({ children }: { children: ReactNode }) {
  const [tasks, setTasks] = useState<Task[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    setError(null)
    try {
      const [t, c] = await Promise.all([api.getTasks(), api.getCategories()])
      setTasks(Array.isArray(t) ? t : [])
      setCategories(Array.isArray(c) ? c : [])
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load data")
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void refresh()
  }, [refresh])

  const createTask = useCallback(
    async (task: NewTask) => {
      await api.createTask(task)
      await refresh()
    },
    [refresh],
  )

  const completeTask = useCallback(async (id: number) => {
    // Optimistic update
    setTasks((prev) => prev.map((t) => (t.id === id ? { ...t, completed: true } : t)))
    try {
      await api.completeTask(id)
    } catch {
      setTasks((prev) => prev.map((t) => (t.id === id ? { ...t, completed: false } : t)))
      throw new Error("Could not complete task")
    }
  }, [])

  const deleteTask = useCallback(async (id: number) => {
    const snapshot = await new Promise<Task[]>((resolve) => {
      setTasks((prev) => {
        resolve(prev)
        return prev.filter((t) => t.id !== id)
      })
    })
    try {
      await api.deleteTask(id)
    } catch {
      setTasks(snapshot)
      throw new Error("Could not delete task")
    }
  }, [])

  const createCategory = useCallback(async (name: string) => {
    const created = await api.createCategory(name)
    setCategories((prev) => [...prev, created])
    return created
  }, [])

  const deleteCategory = useCallback(async (id: number) => {
    setCategories((prev) => prev.filter((c) => c.id !== id))
    await api.deleteCategory(id)
  }, [])

  const value = useMemo<DataContextValue>(
    () => ({
      tasks,
      categories,
      loading,
      error,
      refresh,
      createTask,
      completeTask,
      deleteTask,
      createCategory,
      deleteCategory,
    }),
    [tasks, categories, loading, error, refresh, createTask, completeTask, deleteTask, createCategory, deleteCategory],
  )

  return <DataContext.Provider value={value}>{children}</DataContext.Provider>
}

export function useData() {
  const ctx = useContext(DataContext)
  if (!ctx) throw new Error("useData must be used within DataProvider")
  return ctx
}

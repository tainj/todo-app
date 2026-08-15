import { useMemo, useState, useEffect, useCallback } from "react"
import { ClipboardList, Plus, RefreshCw } from "lucide-react"
import { useData } from "../context/DataContext"
import { GROUP_LABELS, GROUP_ORDER, groupForDeadline, type DeadlineGroup } from "../lib/date"
import type { Task } from "../lib/types"
import { TaskCard } from "../components/TaskCard"
import { Button, Spinner } from "../components/ui"
import { haptic } from "../lib/telegram"

export function TaskListPage({ onCreate }: { onCreate: () => void }) {
  const { tasks, history, categories, loading, error, refresh, loadHistory, completeTask, deleteTask } = useData()
  const [categoryFilter, setCategoryFilter] = useState<number | "all">("all")
  const [showCompleted, setShowCompleted] = useState(false)
  const [refreshing, setRefreshing] = useState(false)

  const categoryMap = useMemo(() => new Map(categories.map((c) => [c.id, c])), [categories])

  useEffect(() => {
    if (showCompleted) void loadHistory()
  }, [showCompleted, loadHistory])

  const allTasks = useMemo(() => showCompleted ? [...tasks, ...history] : tasks, [tasks, history, showCompleted])

  const filtered = useMemo(() => {
    return allTasks.filter((t) => {
      if (!showCompleted && t.completed) return false
      if (categoryFilter !== "all" && t.category_id !== categoryFilter) return false
      return true
    })
  }, [allTasks, categoryFilter, showCompleted])

  const grouped = useMemo(() => {
    const map = new Map<DeadlineGroup, Task[]>()
    for (const t of filtered) {
      const g = groupForDeadline(t.deadline)
      const arr = map.get(g) ?? []
      arr.push(t)
      map.set(g, arr)
    }
    for (const arr of map.values()) {
      arr.sort((a, b) => {
        if (!a.deadline) return 1
        if (!b.deadline) return -1
        return new Date(a.deadline).getTime() - new Date(b.deadline).getTime()
      })
    }
    return map
  }, [filtered])

  const activeCount = tasks.filter((t) => !t.completed).length

  async function handleRefresh() {
    setRefreshing(true)
    haptic("light")
    await refresh()
    if (showCompleted) await loadHistory()
    setRefreshing(false)
  }

  return (
    <div className="flex flex-col">
      <header className="sticky top-0 z-10 bg-bg/95 px-4 pb-3 pt-4 backdrop-blur">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-foreground">Tasks</h1>
            <p className="text-[13px] text-muted">
              {activeCount} active {activeCount === 1 ? "task" : "tasks"}
            </p>
          </div>
          <button
            type="button"
            onClick={handleRefresh}
            aria-label="Refresh"
            className="flex h-10 w-10 items-center justify-center rounded-full bg-surface text-muted active:bg-surface-elevated"
          >
            <RefreshCw className={refreshing ? "h-5 w-5 animate-spin" : "h-5 w-5"} />
          </button>
        </div>

        <div className="-mx-4 mt-3 flex gap-2 overflow-x-auto px-4 pb-1">
          <FilterChip active={categoryFilter === "all"} onClick={() => setCategoryFilter("all")}>
            All
          </FilterChip>
          {categories.map((c) => (
            <FilterChip key={c.id} active={categoryFilter === c.id} onClick={() => setCategoryFilter(c.id)}>
              {c.name}
            </FilterChip>
          ))}
          <FilterChip active={showCompleted} onClick={() => setShowCompleted((v) => !v)}>
            {showCompleted ? "Hiding done" : "Show done"}
          </FilterChip>
        </div>
      </header>

      <div className="px-4 pb-24 pt-1">
        {loading ? (
          <div className="flex justify-center py-20">
            <Spinner className="h-6 w-6 text-muted" />
          </div>
        ) : error ? (
          <div className="flex flex-col items-center gap-4 py-20 text-center">
            <p className="text-[15px] text-danger text-balance">{error}</p>
            <Button variant="secondary" onClick={handleRefresh}>
              Try again
            </Button>
          </div>
        ) : filtered.length === 0 ? (
          <EmptyState onCreate={onCreate} hasTasks={tasks.length > 0} />
        ) : (
          <div className="flex flex-col gap-6">
            {GROUP_ORDER.map((group) => {
              const items = grouped.get(group)
              if (!items || items.length === 0) return null
              return (
                <section key={group}>
                  <h2
                    className={
                      group === "overdue"
                        ? "mb-2 px-1 text-[13px] font-semibold uppercase tracking-wide text-danger"
                        : "mb-2 px-1 text-[13px] font-semibold uppercase tracking-wide text-muted"
                    }
                  >
                    {GROUP_LABELS[group]}
                  </h2>
                  <div className="flex flex-col gap-2">
                    {items.map((task) => (
                      <TaskCard
                        key={task.id}
                        task={task}
                        category={task.category_id ? categoryMap.get(task.category_id) : undefined}
                        onComplete={completeTask}
                        onDelete={deleteTask}
                      />
                    ))}
                  </div>
                </section>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

function FilterChip({
  active,
  onClick,
  children,
}: {
  active: boolean
  onClick: () => void
  children: React.ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={
        active
          ? "flex-shrink-0 rounded-full bg-accent px-4 py-1.5 text-[13px] font-medium text-accent-foreground"
          : "flex-shrink-0 rounded-full bg-surface px-4 py-1.5 text-[13px] font-medium text-muted active:bg-surface-elevated"
      }
    >
      {children}
    </button>
  )
}

function EmptyState({ onCreate, hasTasks }: { onCreate: () => void; hasTasks: boolean }) {
  return (
    <div className="flex flex-col items-center gap-4 py-20 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-surface">
        <ClipboardList className="h-8 w-8 text-muted" />
      </div>
      <div>
        <p className="text-[16px] font-medium text-foreground">{hasTasks ? "Nothing here" : "No tasks yet"}</p>
        <p className="mt-1 text-[14px] text-muted text-balance">
          {hasTasks ? "Try a different filter or add a new task." : "Create your first task to get started."}
        </p>
      </div>
      <Button onClick={onCreate} className="mt-1">
        <Plus className="h-4 w-4" />
        New task
      </Button>
    </div>
  )
}
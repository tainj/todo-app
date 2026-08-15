import { useState } from "react"
import { Bell, Check, Clock, Repeat, Trash2 } from "lucide-react"
import type { Category, Task } from "../lib/types"
import { formatDeadline, groupForDeadline } from "../lib/date"
import { haptic, notify } from "../lib/telegram"
import { Spinner } from "./ui"

const RECURRENCE_LABEL: Record<string, string> = {
  DAILY: "Daily",
  WEEKLY: "Weekly",
}

export function TaskCard({
  task,
  category,
  onComplete,
  onDelete,
}: {
  task: Task
  category?: Category
  onComplete: (id: number) => Promise<void>
  onDelete: (id: number) => Promise<void>
}) {
  const [busy, setBusy] = useState<"complete" | "delete" | null>(null)
  const overdue = groupForDeadline(task.deadline) === "overdue" && !task.completed

  async function handleComplete() {
    if (busy || task.completed) return
    setBusy("complete")
    haptic("medium")
    try {
      await onComplete(task.id)
      notify("success")
    } catch {
      notify("error")
    } finally {
      setBusy(null)
    }
  }

  async function handleDelete() {
    if (busy) return
    setBusy("delete")
    haptic("light")
    try {
      await onDelete(task.id)
    } catch {
      notify("error")
    } finally {
      setBusy(null)
    }
  }

  return (
    <div className="flex items-start gap-3 rounded-[var(--radius)] bg-surface px-4 py-3.5">
      <button
        type="button"
        onClick={handleComplete}
        disabled={task.completed || busy === "complete"}
        aria-label={task.completed ? "Completed" : "Mark complete"}
        className={
          task.completed
            ? "mt-0.5 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-success text-accent-foreground"
            : "mt-0.5 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full border-2 border-muted"
        }
      >
        {busy === "complete" ? (
          <Spinner className="h-3.5 w-3.5 text-muted" />
        ) : task.completed ? (
          <Check className="h-4 w-4" strokeWidth={3} />
        ) : null}
      </button>

      <div className="min-w-0 flex-1">
        <p
          className={
            task.completed
              ? "text-[15px] font-medium text-muted line-through"
              : "text-[15px] font-medium text-foreground text-pretty"
          }
        >
          {task.title}
        </p>
        {task.description && (
          <p className="mt-0.5 line-clamp-2 text-[13px] text-muted text-pretty">{task.description}</p>
        )}

        <div className="mt-2 flex flex-wrap items-center gap-x-3 gap-y-1.5 text-[12px]">
          {task.deadline && (
            <span className={overdue ? "flex items-center gap-1 text-danger" : "flex items-center gap-1 text-muted"}>
              <Clock className="h-3.5 w-3.5" />
              {formatDeadline(task.deadline)}
            </span>
          )}
          {task.recurrence && task.recurrence !== "NONE" && (
            <span className="flex items-center gap-1 text-muted">
              <Repeat className="h-3.5 w-3.5" />
              {RECURRENCE_LABEL[task.recurrence]}
            </span>
          )}
          {task.reminder_offsets && task.reminder_offsets.length > 0 && (
            <span className="flex items-center gap-1 text-muted">
              <Bell className="h-3.5 w-3.5" />
              {task.reminder_offsets.length}
            </span>
          )}
          {category && (
            <span className="rounded-full bg-surface-elevated px-2 py-0.5 text-[11px] text-muted">{category.name}</span>
          )}
        </div>
      </div>

      <button
        type="button"
        onClick={handleDelete}
        disabled={busy === "delete"}
        aria-label="Delete task"
        className="mt-0.5 flex h-6 w-6 flex-shrink-0 items-center justify-center text-muted active:text-danger"
      >
        {busy === "delete" ? <Spinner className="h-3.5 w-3.5" /> : <Trash2 className="h-4 w-4" />}
      </button>
    </div>
  )
}

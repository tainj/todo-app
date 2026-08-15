export type DeadlineGroup = "overdue" | "today" | "tomorrow" | "later" | "none"

export const GROUP_ORDER: DeadlineGroup[] = ["overdue", "today", "tomorrow", "later", "none"]

export const GROUP_LABELS: Record<DeadlineGroup, string> = {
  overdue: "Overdue",
  today: "Today",
  tomorrow: "Tomorrow",
  later: "Later",
  none: "No deadline",
}

function startOfDay(d: Date): number {
  return new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime()
}

export function groupForDeadline(deadline?: string | null): DeadlineGroup {
  if (!deadline) return "none"
  const date = new Date(deadline)
  if (Number.isNaN(date.getTime())) return "none"

  const now = new Date()
  const today = startOfDay(now)
  const target = startOfDay(date)
  const oneDay = 24 * 60 * 60 * 1000

  if (date.getTime() < now.getTime() && target < today) return "overdue"
  if (target === today) return date.getTime() < now.getTime() ? "overdue" : "today"
  if (target === today + oneDay) return "tomorrow"
  return "later"
}

export function formatDeadline(deadline?: string | null): string {
  if (!deadline) return ""
  const date = new Date(deadline)
  if (Number.isNaN(date.getTime())) return ""

  const time = date.toLocaleTimeString(undefined, { hour: "2-digit", minute: "2-digit" })
  const group = groupForDeadline(deadline)

  if (group === "today" || (group === "overdue" && startOfDay(date) === startOfDay(new Date()))) {
    return time
  }
  if (group === "tomorrow") return `Tomorrow, ${time}`

  const sameYear = date.getFullYear() === new Date().getFullYear()
  const dateStr = date.toLocaleDateString(undefined, {
    month: "short",
    day: "numeric",
    year: sameYear ? undefined : "numeric",
  })
  return `${dateStr}, ${time}`
}

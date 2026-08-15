import { ListTodo, Plus, Settings } from "lucide-react"
import { haptic } from "../lib/telegram"

export type View = "tasks" | "create" | "settings"

const items: { view: View; label: string; icon: typeof ListTodo }[] = [
  { view: "tasks", label: "Tasks", icon: ListTodo },
  { view: "create", label: "New", icon: Plus },
  { view: "settings", label: "Settings", icon: Settings },
]

export function BottomNav({ current, onChange }: { current: View; onChange: (v: View) => void }) {
  return (
    <nav className="sticky bottom-0 z-10 border-t border-border bg-surface/95 backdrop-blur pb-[env(safe-area-inset-bottom)]">
      <div className="mx-auto flex max-w-md items-stretch justify-around">
        {items.map(({ view, label, icon: Icon }) => {
          const active = current === view
          const isCreate = view === "create"
          return (
            <button
              key={view}
              type="button"
              aria-current={active ? "page" : undefined}
              onClick={() => {
                haptic("light")
                onChange(view)
              }}
              className="flex flex-1 flex-col items-center gap-1 py-2.5"
            >
              <span
                className={
                  isCreate
                    ? "flex h-9 w-9 items-center justify-center rounded-full bg-accent text-accent-foreground"
                    : "flex h-9 w-9 items-center justify-center"
                }
              >
                <Icon
                  className={isCreate ? "h-5 w-5" : active ? "h-6 w-6 text-accent" : "h-6 w-6 text-muted"}
                  strokeWidth={active || isCreate ? 2.4 : 2}
                />
              </span>
              <span className={active && !isCreate ? "text-[11px] text-accent" : "text-[11px] text-muted"}>
                {label}
              </span>
            </button>
          )
        })}
      </div>
    </nav>
  )
}

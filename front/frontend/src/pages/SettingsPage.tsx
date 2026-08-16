import { useEffect, useState } from "react"
import { Bell, LogOut, Radio, Trash2, User as UserIcon, Send } from "lucide-react"
import { useAuth } from "../context/AuthContext"
import { useData } from "../context/DataContext"
import { api, ApiError } from "../lib/api"
import type { User } from "../lib/types"
import { haptic, notify } from "../lib/telegram"
import { Spinner } from "../components/ui"

export function SettingsPage() {
  const { logout } = useAuth()
  const { categories, deleteCategory } = useData()

  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [savingKey, setSavingKey] = useState<"notify_telegram" | "notify_websocket" | null>(null)
  const [connectingTg, setConnectingTg] = useState(false)

  useEffect(() => {
    let active = true
    api
      .getMe()
      .then((u) => { if (active) setUser(u) })
      .catch((err) => { if (active) setError(err instanceof ApiError ? err.message : "Failed to load profile") })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [])

  async function toggle(key: "notify_telegram" | "notify_websocket") {
    if (!user || savingKey) return
    const next = { ...user, [key]: !user[key] }
    setUser(next)
    setSavingKey(key)
    haptic("light")
    try {
      const updated = await api.updateSettings({
        notify_telegram: next.notify_telegram,
        notify_websocket: next.notify_websocket,
      })
      setUser(updated ?? next)
      notify("success")
    } catch (err) {
      setUser(user)
      notify("error")
      setError(err instanceof ApiError ? err.message : "Could not save settings")
    } finally {
      setSavingKey(null)
    }
  }

  async function connectTelegram() {
    setConnectingTg(true)
    haptic("light")
    try {
      const { url } = await api.getTelegramLink()
      window.location.href = url
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not get Telegram link")
    } finally {
      setConnectingTg(false)
    }
  }

  async function handleDeleteCategory(id: number) {
    haptic("light")
    try {
      await deleteCategory(id)
    } catch {
      notify("error")
    }
  }

  return (
    <div className="flex flex-col">
      <header className="sticky top-0 z-10 bg-bg/95 px-4 pb-3 pt-4 backdrop-blur">
        <h1 className="text-2xl font-bold text-foreground">Settings</h1>
      </header>

      <div className="flex flex-col gap-6 px-4 pb-24 pt-2">
        {loading ? (
          <div className="flex justify-center py-20">
            <Spinner className="h-6 w-6 text-muted" />
          </div>
        ) : (
          <>
            <section className="flex items-center gap-3 rounded-[var(--radius)] bg-surface px-4 py-4">
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-accent">
                <UserIcon className="h-6 w-6 text-accent-foreground" />
              </div>
              <div className="min-w-0">
                <p className="truncate text-[16px] font-semibold text-foreground">
                  {user?.username ?? "Account"}
                </p>
                <p className="text-[13px] text-muted">
                  {user?.telegram_chat_id ? "Telegram connected ✓" : "Telegram not connected"}
                </p>
              </div>
            </section>

            {!user?.telegram_chat_id && (
              <button
                type="button"
                onClick={connectTelegram}
                disabled={connectingTg}
                className="flex items-center justify-center gap-2 rounded-[var(--radius)] bg-accent px-4 py-3.5 text-[15px] font-medium text-accent-foreground active:opacity-80"
              >
                <Send className="h-5 w-5" />
                {connectingTg ? "Opening..." : "Connect Telegram"}
              </button>
            )}

            <section>
              <h2 className="mb-2 px-1 text-[13px] font-semibold uppercase tracking-wide text-muted">
                Notifications
              </h2>
              <div className="overflow-hidden rounded-[var(--radius)] bg-surface">
                <ToggleRow
                  icon={<Bell className="h-5 w-5 text-accent" />}
                  label="Telegram alerts"
                  description="Reminders via the bot"
                  checked={user?.notify_telegram ?? false}
                  busy={savingKey === "notify_telegram"}
                  onChange={() => toggle("notify_telegram")}
                />
                <div className="mx-4 h-px bg-border" />
                <ToggleRow
                  icon={<Radio className="h-5 w-5 text-accent" />}
                  label="Live updates"
                  description="Real-time in-app notifications"
                  checked={user?.notify_websocket ?? false}
                  busy={savingKey === "notify_websocket"}
                  onChange={() => toggle("notify_websocket")}
                />
              </div>
            </section>

            <section>
              <h2 className="mb-2 px-1 text-[13px] font-semibold uppercase tracking-wide text-muted">
                Categories
              </h2>
              <div className="overflow-hidden rounded-[var(--radius)] bg-surface">
                {categories.length === 0 ? (
                  <p className="px-4 py-4 text-[14px] text-muted">
                    No categories yet. Add them when creating a task.
                  </p>
                ) : (
                  categories.map((c, i) => (
                    <div key={c.id}>
                      {i > 0 && <div className="mx-4 h-px bg-border" />}
                      <div className="flex items-center justify-between px-4 py-3.5">
                        <span className="text-[15px] text-foreground">{c.name}</span>
                        <button
                          type="button"
                          onClick={() => handleDeleteCategory(c.id)}
                          aria-label={`Delete ${c.name}`}
                          className="flex h-8 w-8 items-center justify-center text-muted active:text-danger"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </section>

            {error && (
              <p role="alert" className="rounded-[var(--radius)] bg-danger/15 px-4 py-3 text-[14px] text-danger">
                {error}
              </p>
            )}

            <button
              type="button"
              onClick={() => { haptic("medium"); logout() }}
              className="flex items-center justify-center gap-2 rounded-[var(--radius)] bg-surface px-4 py-3.5 text-[15px] font-medium text-danger active:bg-surface-elevated"
            >
              <LogOut className="h-5 w-5" />
              Sign Out
            </button>
          </>
        )}
      </div>
    </div>
  )
}

function ToggleRow({
  icon, label, description, checked, busy, onChange,
}: {
  icon: React.ReactNode
  label: string
  description: string
  checked: boolean
  busy: boolean
  onChange: () => void
}) {
  return (
    <div className="flex items-center gap-3 px-4 py-3.5">
      <div className="flex h-9 w-9 items-center justify-center rounded-full bg-surface-elevated">{icon}</div>
      <div className="min-w-0 flex-1">
        <p className="text-[15px] font-medium text-foreground">{label}</p>
        <p className="text-[13px] text-muted">{description}</p>
      </div>
      <button
        type="button"
        role="switch"
        aria-checked={checked}
        aria-label={label}
        disabled={busy}
        onClick={onChange}
        className={checked
          ? "relative h-7 w-12 flex-shrink-0 rounded-full bg-accent transition-colors"
          : "relative h-7 w-12 flex-shrink-0 rounded-full bg-surface-elevated transition-colors"
        }
      >
        <span className={checked
          ? "absolute top-1 h-5 w-5 translate-x-0 rounded-full bg-accent-foreground transition-transform"
          : "absolute top-1 h-5 w-5 -translate-x-5 rounded-full bg-muted transition-transform"
        } />
      </button>
    </div>
  )
}
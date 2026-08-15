import { useState, type FormEvent } from "react"
import { CheckCircle2 } from "lucide-react"
import { useAuth } from "../context/AuthContext"
import { ApiError } from "../lib/api"
import { notify } from "../lib/telegram"
import { Button, Field, Input } from "../components/ui"

export function AuthPage() {
  const { login, register } = useAuth()
  const [mode, setMode] = useState<"login" | "register">("login")
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (loading) return
    setError(null)

    if (username.trim().length < 3) {
      setError("Username must be at least 3 characters.")
      return
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.")
      return
    }

    setLoading(true)
    try {
      if (mode === "login") {
        await login(username.trim(), password)
      } else {
        await register(username.trim(), password)
      }
      notify("success")
    } catch (err) {
      notify("error")
      setError(err instanceof ApiError ? err.message : "Something went wrong.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-full flex-col justify-center px-6 py-12">
      <div className="mx-auto w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center text-center">
          <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-accent">
            <CheckCircle2 className="h-9 w-9 text-accent-foreground" strokeWidth={2.2} />
          </div>
          <h1 className="text-2xl font-bold text-foreground">Tasks</h1>
          <p className="mt-1 text-[15px] text-muted text-balance">
            {mode === "login" ? "Sign in to manage your tasks" : "Create an account to get started"}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Field label="Username">
            <Input
              type="text"
              autoCapitalize="none"
              autoCorrect="off"
              placeholder="your_username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
            />
          </Field>
          <Field label="Password">
            <Input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
            />
          </Field>

          {error && (
            <p role="alert" className="rounded-[var(--radius)] bg-danger/15 px-4 py-3 text-[14px] text-danger">
              {error}
            </p>
          )}

          <Button type="submit" loading={loading} className="mt-2">
            {mode === "login" ? "Sign In" : "Create Account"}
          </Button>
        </form>

        <button
          type="button"
          onClick={() => {
            setMode(mode === "login" ? "register" : "login")
            setError(null)
          }}
          className="mt-6 w-full text-center text-[14px] text-muted"
        >
          {mode === "login" ? (
            <>
              No account? <span className="text-accent">Register</span>
            </>
          ) : (
            <>
              Already registered? <span className="text-accent">Sign in</span>
            </>
          )}
        </button>
      </div>
    </div>
  )
}

import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react"
import { api, clearToken, getToken, setToken, UNAUTHORIZED_EVENT } from "../lib/api"

interface AuthContextValue {
  isAuthenticated: boolean
  ready: boolean
  login: (username: string, password: string) => Promise<void>
  register: (username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(() => getToken())
  const [ready, setReady] = useState(false)

  useEffect(() => {
    setReady(true)
  }, [])

  const logout = useCallback(() => {
    clearToken()
    setTokenState(null)
  }, [])

  useEffect(() => {
    const handler = () => logout()
    window.addEventListener(UNAUTHORIZED_EVENT, handler)
    return () => window.removeEventListener(UNAUTHORIZED_EVENT, handler)
  }, [logout])

  const login = useCallback(async (username: string, password: string) => {
    const { token } = await api.login(username, password)
    setToken(token)
    setTokenState(token)
  }, [])

  const register = useCallback(
    async (username: string, password: string) => {
      await api.register(username, password)
      // Auto-login after successful registration.
      await login(username, password)
    },
    [login],
  )

  const value = useMemo<AuthContextValue>(
    () => ({
      isAuthenticated: Boolean(token),
      ready,
      login,
      register,
      logout,
    }),
    [token, ready, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error("useAuth must be used within AuthProvider")
  return ctx
}

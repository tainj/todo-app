import type { Category, NewTask, Task, User, UserSettings } from "./types"

const BASE_URL = (import.meta.env.VITE_API_URL ?? "http://localhost:8080/api").replace(/\/$/, "")

const TOKEN_KEY = "todo_jwt"

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export class ApiError extends Error {
  status: number
  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

/** Emitted when a request returns 401 so the app can force a logout. */
export const UNAUTHORIZED_EVENT = "todo:unauthorized"

async function request<T>(path: string, options: RequestInit = {}, auth = true): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string> | undefined),
  }

  if (auth) {
    const token = getToken()
    if (token) headers.Authorization = `Bearer ${token}`
  }

  let res: Response
  try {
    res = await fetch(`${BASE_URL}${path}`, { ...options, headers })
  } catch {
    throw new ApiError("Network error. Is the server reachable?", 0)
  }

  if (res.status === 401) {
    window.dispatchEvent(new Event(UNAUTHORIZED_EVENT))
    throw new ApiError("Session expired. Please sign in again.", 401)
  }

  if (!res.ok) {
    let message = `Request failed (${res.status})`
    try {
      const data = await res.json()
      message = data.message || data.error || message
    } catch {
      // ignore parse errors
    }
    throw new ApiError(message, res.status)
  }

  if (res.status === 204) return undefined as T
  const text = await res.text()
  return (text ? JSON.parse(text) : undefined) as T
}

export const api = {
  // Auth
  register: (username: string, password: string) =>
    request<void>("/auth/register", { method: "POST", body: JSON.stringify({ username, password }) }, false),
  login: (username: string, password: string) =>
    request<{ token: string }>("/auth/login", { method: "POST", body: JSON.stringify({ username, password }) }, false),

  // Tasks
  getTasks: () => request<Task[]>("/tasks"),
  createTask: (task: NewTask) => request<Task>("/tasks", { method: "POST", body: JSON.stringify(task) }),
  updateTask: (id: number, task: Partial<NewTask>) =>
    request<Task>(`/tasks/${id}`, { method: "PUT", body: JSON.stringify(task) }),
  deleteTask: (id: number) => request<void>(`/tasks/${id}`, { method: "DELETE" }),
  completeTask: (id: number) => request<Task>(`/tasks/${id}/complete`, { method: "PATCH" }),
  getHistory: () => request<Task[]>("/tasks/history"),

  // Categories
  getCategories: () => request<Category[]>("/categories"),
  createCategory: (name: string) =>
    request<Category>("/categories", { method: "POST", body: JSON.stringify({ name }) }),
  deleteCategory: (id: number) => request<void>(`/categories/${id}`, { method: "DELETE" }),

  // Users
  getMe: () => request<User>("/users/me"),
  updateSettings: (settings: UserSettings) =>
    request<User>("/users/settings", { method: "PATCH", body: JSON.stringify(settings) }),
}

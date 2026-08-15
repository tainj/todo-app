export type Recurrence = "NONE" | "DAILY" | "WEEKLY"

export interface Task {
  id: number
  title: string
  description?: string | null
  deadline?: string | null
  reminder_offsets?: number[] | null
  recurrence: Recurrence
  completed: boolean
  category_id?: number | null
}

export interface Category {
  id: number
  name: string
}

export interface User {
  id: number
  username: string
  notify_telegram: boolean
  notify_websocket: boolean
}

export interface UserSettings {
  notify_telegram: boolean
  notify_websocket: boolean
}

export interface NewTask {
  title: string
  description?: string
  deadline?: string | null
  reminder_offsets: number[]
  recurrence: Recurrence
  category_id?: number | null
}

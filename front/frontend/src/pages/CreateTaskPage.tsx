import { useState, type FormEvent } from "react"
import { Check } from "lucide-react"
import { useData } from "../context/DataContext"
import type { NewTask, Recurrence } from "../lib/types"
import { ApiError } from "../lib/api"
import { notify } from "../lib/telegram"
import { Button, Field, Input, Select, Textarea } from "../components/ui"

const REMINDER_OPTIONS: { value: number; label: string }[] = [
  { value: 0, label: "At time" },
  { value: 15, label: "15 min before" },
  { value: 60, label: "1 hour before" },
  { value: 1440, label: "1 day before" },
]

const RECURRENCE_OPTIONS: { value: Recurrence; label: string }[] = [
  { value: "NONE", label: "Does not repeat" },
  { value: "DAILY", label: "Daily" },
  { value: "WEEKLY", label: "Weekly" },
]

export function CreateTaskPage({ onDone }: { onDone: () => void }) {
  const { categories, createTask, createCategory } = useData()

  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [deadline, setDeadline] = useState("")
  const [reminderOffsets, setReminderOffsets] = useState<number[]>([])
  const [recurrence, setRecurrence] = useState<Recurrence>("NONE")
  const [categoryId, setCategoryId] = useState<number | "">("")
  const [newCategory, setNewCategory] = useState("")
  const [addingCategory, setAddingCategory] = useState(false)

  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  function toggleReminder(value: number) {
    setReminderOffsets((prev) =>
      prev.includes(value) ? prev.filter((v) => v !== value) : [...prev, value].sort((a, b) => a - b),
    )
  }

  async function handleAddCategory() {
    const name = newCategory.trim()
    if (!name || addingCategory) return
    setAddingCategory(true)
    try {
      const created = await createCategory(name)
      setCategoryId(created.id)
      setNewCategory("")
      notify("success")
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not add category")
    } finally {
      setAddingCategory(false)
    }
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (loading) return
    setError(null)

    if (!title.trim()) {
      setError("Please enter a title.")
      return
    }

    const payload: NewTask = {
      title: title.trim(),
      description: description.trim() || undefined,
      // datetime-local has no timezone; convert to ISO for the API.
      deadline: deadline ? new Date(deadline).toISOString() : null,
      reminder_offsets: reminderOffsets,
      recurrence,
      category_id: categoryId === "" ? null : categoryId,
    }

    setLoading(true)
    try {
      await createTask(payload)
      notify("success")
      onDone()
    } catch (err) {
      notify("error")
      setError(err instanceof ApiError ? err.message : "Could not create task")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex flex-col">
      <header className="sticky top-0 z-10 bg-bg/95 px-4 pb-3 pt-4 backdrop-blur">
        <h1 className="text-2xl font-bold text-foreground">New Task</h1>
      </header>

      <form onSubmit={handleSubmit} className="flex flex-col gap-5 px-4 pb-28 pt-2">
        <Field label="Title">
          <Input
            type="text"
            placeholder="What needs to be done?"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            autoFocus
            disabled={loading}
          />
        </Field>

        <Field label="Description">
          <Textarea
            rows={3}
            placeholder="Add details (optional)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            disabled={loading}
          />
        </Field>

        <Field label="Deadline">
          <Input
            type="datetime-local"
            value={deadline}
            onChange={(e) => setDeadline(e.target.value)}
            disabled={loading}
          />
        </Field>

        <Field label="Reminders" hint="Notify before the deadline">
          <div className="grid grid-cols-2 gap-2">
            {REMINDER_OPTIONS.map((opt) => {
              const active = reminderOffsets.includes(opt.value)
              return (
                <button
                  key={opt.value}
                  type="button"
                  onClick={() => toggleReminder(opt.value)}
                  disabled={loading}
                  className={
                    active
                      ? "flex items-center justify-between rounded-[var(--radius)] bg-accent/15 px-4 py-3 text-[14px] font-medium text-accent ring-1 ring-accent"
                      : "flex items-center justify-between rounded-[var(--radius)] bg-surface px-4 py-3 text-[14px] text-foreground"
                  }
                >
                  {opt.label}
                  <span
                    className={
                      active
                        ? "flex h-5 w-5 items-center justify-center rounded-md bg-accent text-accent-foreground"
                        : "h-5 w-5 rounded-md border-2 border-muted"
                    }
                  >
                    {active && <Check className="h-3.5 w-3.5" strokeWidth={3} />}
                  </span>
                </button>
              )
            })}
          </div>
        </Field>

        <Field label="Repeat">
          <Select value={recurrence} onChange={(e) => setRecurrence(e.target.value as Recurrence)} disabled={loading}>
            {RECURRENCE_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </Select>
        </Field>

        <Field label="Category">
          <Select
            value={categoryId === "" ? "" : String(categoryId)}
            onChange={(e) => setCategoryId(e.target.value === "" ? "" : Number(e.target.value))}
            disabled={loading}
          >
            <option value="">No category</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </Select>
          <div className="mt-2 flex gap-2">
            <Input
              type="text"
              placeholder="New category"
              value={newCategory}
              onChange={(e) => setNewCategory(e.target.value)}
              disabled={loading || addingCategory}
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.nativeEvent.isComposing && e.keyCode !== 229) {
                  e.preventDefault()
                  void handleAddCategory()
                }
              }}
            />
            <Button
              type="button"
              variant="secondary"
              onClick={handleAddCategory}
              loading={addingCategory}
              disabled={!newCategory.trim()}
              className="flex-shrink-0"
            >
              Add
            </Button>
          </div>
        </Field>

        {error && (
          <p role="alert" className="rounded-[var(--radius)] bg-danger/15 px-4 py-3 text-[14px] text-danger">
            {error}
          </p>
        )}

        <div className="flex gap-3">
          <Button type="button" variant="secondary" onClick={onDone} disabled={loading} className="flex-1">
            Cancel
          </Button>
          <Button type="submit" loading={loading} className="flex-1">
            Create Task
          </Button>
        </div>
      </form>
    </div>
  )
}

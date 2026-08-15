import type { ButtonHTMLAttributes, InputHTMLAttributes, ReactNode, SelectHTMLAttributes, TextareaHTMLAttributes } from "react"

function cn(...classes: (string | false | null | undefined)[]) {
  return classes.filter(Boolean).join(" ")
}

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "ghost" | "danger"
  loading?: boolean
}

export function Button({ variant = "primary", loading, className, children, disabled, ...props }: ButtonProps) {
  const styles: Record<string, string> = {
    primary: "bg-accent text-accent-foreground active:bg-accent/80",
    secondary: "bg-surface-elevated text-foreground active:bg-border",
    ghost: "bg-transparent text-accent active:bg-surface",
    danger: "bg-transparent text-danger active:bg-surface",
  }
  return (
    <button
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-[var(--radius)] px-4 py-3 text-[15px] font-medium transition-colors disabled:opacity-50",
        styles[variant],
        className,
      )}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? <Spinner /> : children}
    </button>
  )
}

export function Spinner({ className }: { className?: string }) {
  return (
    <span
      className={cn("h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent", className)}
      aria-hidden="true"
    />
  )
}

export function Input({ className, ...props }: InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      className={cn(
        "w-full rounded-[var(--radius)] bg-surface px-4 py-3 text-[15px] text-foreground placeholder:text-muted outline-none ring-accent/60 focus:ring-2",
        className,
      )}
      {...props}
    />
  )
}

export function Textarea({ className, ...props }: TextareaHTMLAttributes<HTMLTextAreaElement>) {
  return (
    <textarea
      className={cn(
        "w-full resize-none rounded-[var(--radius)] bg-surface px-4 py-3 text-[15px] text-foreground placeholder:text-muted outline-none ring-accent/60 focus:ring-2",
        className,
      )}
      {...props}
    />
  )
}

export function Select({ className, children, ...props }: SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select
      className={cn(
        "w-full appearance-none rounded-[var(--radius)] bg-surface px-4 py-3 text-[15px] text-foreground outline-none ring-accent/60 focus:ring-2",
        className,
      )}
      {...props}
    >
      {children}
    </select>
  )
}

export function Field({ label, children, hint }: { label: string; children: ReactNode; hint?: string }) {
  return (
    <label className="flex flex-col gap-2">
      <span className="px-1 text-[13px] font-medium uppercase tracking-wide text-muted">{label}</span>
      {children}
      {hint && <span className="px-1 text-[12px] text-muted">{hint}</span>}
    </label>
  )
}

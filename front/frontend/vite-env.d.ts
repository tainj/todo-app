/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

interface TelegramWebApp {
  ready: () => void
  expand: () => void
  close: () => void
  colorScheme: "light" | "dark"
  themeParams: Record<string, string>
  initData: string
  initDataUnsafe: {
    user?: {
      id: number
      first_name?: string
      last_name?: string
      username?: string
    }
  }
  BackButton: {
    show: () => void
    hide: () => void
    onClick: (cb: () => void) => void
    offClick: (cb: () => void) => void
  }
  MainButton: {
    setText: (text: string) => void
    show: () => void
    hide: () => void
    onClick: (cb: () => void) => void
    offClick: (cb: () => void) => void
    showProgress: (leaveActive?: boolean) => void
    hideProgress: () => void
  }
  HapticFeedback?: {
    impactOccurred: (style: "light" | "medium" | "heavy") => void
    notificationOccurred: (type: "error" | "success" | "warning") => void
  }
  setHeaderColor?: (color: string) => void
  setBackgroundColor?: (color: string) => void
}

interface Window {
  Telegram?: {
    WebApp: TelegramWebApp
  }
}

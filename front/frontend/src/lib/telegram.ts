export function getWebApp(): TelegramWebApp | null {
  return typeof window !== "undefined" && window.Telegram?.WebApp ? window.Telegram.WebApp : null
}

/** Initialize the Telegram Mini App: expand to full height and sync theme colors. */
export function initTelegram() {
  const tg = getWebApp()
  if (!tg) return
  try {
    tg.ready()
    tg.expand()
    tg.setHeaderColor?.("#17212b")
    tg.setBackgroundColor?.("#17212b")
  } catch {
    // no-op outside Telegram
  }
}

export function haptic(type: "light" | "medium" | "heavy" = "light") {
  getWebApp()?.HapticFeedback?.impactOccurred(type)
}

export function notify(type: "error" | "success" | "warning") {
  getWebApp()?.HapticFeedback?.notificationOccurred(type)
}

/** Wire the Telegram hardware Back button. Returns a cleanup function. */
export function useBackButton(show: boolean, onBack: () => void): () => void {
  const tg = getWebApp()
  if (!tg) return () => {}
  const handler = () => onBack()
  if (show) {
    tg.BackButton.onClick(handler)
    tg.BackButton.show()
  } else {
    tg.BackButton.hide()
  }
  return () => {
    tg.BackButton.offClick(handler)
    tg.BackButton.hide()
  }
}

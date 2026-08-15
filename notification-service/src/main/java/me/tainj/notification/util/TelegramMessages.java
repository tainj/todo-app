package me.tainj.notification.util;

import me.tainj.notification.model.NotificationEvent;

public class TelegramMessages {
    public static final String REMINDER = "🔔 *%s*\n%s\n\n🏷️ %s\n⏰ Дедлайн: %s";
    public static final String WELCOME = "👋 Уведомления подключены!";

    public static String createMessage(NotificationEvent event) {
        return String.format(
                REMINDER,
                event.getTaskTitle(),
                event.getCategoryName() != null ? event.getCategoryName() : "Без категории",
                event.getTaskDescription(),
                "20:31"
        );
    }
}
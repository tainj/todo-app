package me.tainj.notification.util;

import me.tainj.notification.model.NotificationEvent;

public class TelegramMessages {
    public static final String REMINDER = "🔔 *%s*\n" +
            "%s\n\n" +
            "🏷️ %s\n" +
            "⏰ Deadline: %s\n" +
            "⏱️ Reminder in %d minutes";

    public static final String WELCOME = "👋 Notifications connected!";

    public static String createMessage(NotificationEvent event) {
        return String.format(
                REMINDER,
                event.getTaskTitle(),
                event.getTaskDescription() != null ? event.getTaskDescription() : "",
                event.getCategoryName() != null ? event.getCategoryName() : "No category",
                event.getDueDate(),
                event.getMinuteBefore()
        );
    }
}
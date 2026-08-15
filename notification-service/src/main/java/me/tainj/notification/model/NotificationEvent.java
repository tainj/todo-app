package me.tainj.notification.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationEvent {
    private Long taskId;
    private Long userId;
    private Long chatId;
    private String taskTitle;
    private String taskDescription;
    private LocalDateTime dueDate;
    private NotificationType type;
    private String categoryName;

    public enum NotificationType {
        TELEGRAM, WEBSOCKET
    }
}
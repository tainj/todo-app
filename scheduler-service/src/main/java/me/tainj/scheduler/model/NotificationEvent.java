package me.tainj.scheduler.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationEvent {
    private Long taskId;
    private Long userId;
    private Long chatId;
    private String taskTitle;
    private String message;
    private LocalDateTime dueDate;
}
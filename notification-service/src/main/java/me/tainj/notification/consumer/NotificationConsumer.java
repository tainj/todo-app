package me.tainj.notification.consumer;

import me.tainj.notification.model.NotificationEvent;
import me.tainj.notification.service.TelegramService;
import me.tainj.notification.service.WebSocketService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final TelegramService telegramService;
    private final WebSocketService webSocketService;

    public NotificationConsumer(TelegramService telegramService, WebSocketService webSocketService) {
        this.telegramService = telegramService;
        this.webSocketService = webSocketService;
    }

    @KafkaListener(topics = "telegram-notifications", groupId = "telegram-group")
    public void handleTelegram(NotificationEvent event) {
        telegramService.send(event);
    }

    @KafkaListener(topics = "websocket-notifications", groupId = "websocket-group")
    public void handleWebSocket(NotificationEvent event) {
        webSocketService.send(event);
    }
}
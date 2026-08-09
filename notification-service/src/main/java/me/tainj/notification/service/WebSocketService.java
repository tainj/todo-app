package me.tainj.notification.service;

import me.tainj.notification.model.NotificationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void send(NotificationEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.getUserId(),
                event
        );
    }
}
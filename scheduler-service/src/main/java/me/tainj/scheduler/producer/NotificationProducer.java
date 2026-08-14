package me.tainj.scheduler.producer;

import me.tainj.scheduler.model.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTelegram(NotificationEvent event) {
        kafkaTemplate.send("telegram-notifications", event);
        log.info("Sent telegram notification for task {}", event.getTaskId());
    }

    public void sendWebSocket(NotificationEvent event) {
        kafkaTemplate.send("websocket-notifications", event);
        log.info("Sent websocket notification for task {}", event.getTaskId());
    }
}
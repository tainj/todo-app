package me.tainj.scheduler.service;

import me.tainj.scheduler.model.NotificationEvent;
import me.tainj.scheduler.model.Task;
import me.tainj.scheduler.producer.NotificationProducer;
import me.tainj.scheduler.repository.TaskRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ReminderService {
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationProducer notificationProducer;
    private final TaskRepository taskRepository;

    public ReminderService(RedisTemplate<String, String> redisTemplate, NotificationProducer notificationProducer, TaskRepository taskRepository) {
        this.redisTemplate = redisTemplate;
        this.notificationProducer = notificationProducer;
        this.taskRepository = taskRepository;
    }

    public void processOverdueTasks() {
        List<Task> tasks = taskRepository.findOverdueTasks(OffsetDateTime.now());
        for (Task task : tasks) {
            String key = "reminder:sent:" + task.getId();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                continue;
            }
            NotificationEvent event = task.toEvent();
            notificationProducer.sendTelegram(event);
            notificationProducer.sendWebSocket(event);
            redisTemplate.opsForValue().set(key, "sent", Duration.ofHours(24));
        }
    }
}


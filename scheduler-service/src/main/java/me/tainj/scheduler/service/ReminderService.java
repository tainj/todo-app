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

    public void processReminders() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Task> tasks = taskRepository.findTasksWithDeadlineBetween(now, now.plusMinutes(1440));

        for (Task task : tasks) {
            if (task.getReminderOffsets() == null) continue;

            for (int offset : task.getReminderOffsets()) {
                OffsetDateTime reminderTime = task.getDeadline().minusMinutes(offset);

                if (reminderTime.isBefore(now.minusMinutes(1)) || reminderTime.isAfter(now.plusMinutes(1))) {
                    continue;
                }

                String key = "reminder:" + task.getId() + ":" + offset;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) continue;

                NotificationEvent event = task.toEvent();
                event.setMinuteBefore(offset);

                if (Boolean.TRUE.equals(task.getUser().isNotifyTelegram())) {
                    notificationProducer.sendTelegram(event);
                }
                if (Boolean.TRUE.equals(task.getUser().isNotifyWebsocket())) {
                    notificationProducer.sendWebSocket(event);
                }

                redisTemplate.opsForValue().set(key, "sent", Duration.ofMinutes(offset + 5));
            }
        }
    }
}
package me.tainj.scheduler.scheduler;

import me.tainj.scheduler.service.ReminderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {

    private final ReminderService reminderService;

    public ReminderScheduler(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(fixedRate = 60000) // каждую минуту
    public void checkReminders() {
        reminderService.processOverdueTasks();
    }
}
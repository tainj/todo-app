package me.tainj.scheduler.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "deadline")
    private OffsetDateTime deadline;

    @Column(name = "reminder_offsets", columnDefinition = "integer[]")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.ARRAY)
    private List<Integer> reminderOffsets;

    @Column(name = "recurrence", length = 20)
    @Enumerated(EnumType.STRING)
    private Recurrence recurrence = Recurrence.NONE;

    public enum Recurrence {
        NONE, DAILY, WEEKLY
    }

    public NotificationEvent toEvent() {
        NotificationEvent event = new NotificationEvent();
        event.setTaskId(id);
        event.setUserId(user.getId());
        event.setChatId(user.getTelegramChatId());
        event.setTaskTitle(title);
        event.setTaskDescription(description);
        event.setDueDate(deadline != null ? deadline.toLocalDateTime() : null);
        return event;
    }
}
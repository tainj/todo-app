package me.tainj.scheduler.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
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

    public NotificationEvent toEvent() {
        NotificationEvent event = new NotificationEvent();
        event.setTaskId(id);
        event.setUserId(user.getId());
        event.setChatId(user.getTelegramChatId());
        event.setTaskTitle(title);
        event.setMessage(description);
        event.setDueDate(createdAt.toLocalDateTime());
        return event;
    }
}
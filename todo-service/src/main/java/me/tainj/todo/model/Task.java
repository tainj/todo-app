package me.tainj.todo.model;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tainj.todo.dto.response.TaskResponse;
import org.hibernate.annotations.CreationTimestamp;
import java.util.List;

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

    @Column(name = "deadline")
    private OffsetDateTime deadline;

    @Column(name = "reminder_offsets", columnDefinition = "integer[]")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.ARRAY)
    private List<Integer> reminderOffsets;

    @Column(name = "recurrence", length = 20)
    @Enumerated(EnumType.STRING)
    private Recurrence recurrence = Recurrence.NONE;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public TaskResponse toResponse() {
        return new TaskResponse(id, title, description, completed, createdAt, deadline, reminderOffsets, recurrence);
    }

    public enum Recurrence {
        NONE, DAILY, WEEKLY
    }
}
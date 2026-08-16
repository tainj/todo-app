package me.tainj.todo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "task_completions")
@Data
@NoArgsConstructor
public class TaskCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "completed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime completedAt;

    @Column(name = "scheduled_for")
    private OffsetDateTime scheduledFor;
}
package me.tainj.todo.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tainj.todo.dto.response.TaskResponse;
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
    @JoinColumn(name = "user_id")
    private User user;

    public TaskResponse toResponse() {
        return new TaskResponse(id, title, description, completed, createdAt);
    }
}
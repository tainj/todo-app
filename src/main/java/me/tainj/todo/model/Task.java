package me.tainj.todo.model;

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

    private String title;
    private String description;
    private boolean completed;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;
}
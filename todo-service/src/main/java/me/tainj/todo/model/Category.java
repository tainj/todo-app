package me.tainj.todo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tainj.todo.dto.response.CategoryResponse;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CategoryResponse toResponse() {
        return new CategoryResponse(id, name, user == null);
    }

    public boolean isDefault() {
        return user == null;
    }
}

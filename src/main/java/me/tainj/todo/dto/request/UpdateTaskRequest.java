package me.tainj.todo.dto.request;

public record UpdateTaskRequest(
        String title,
        String description,
        boolean completed
) {
}
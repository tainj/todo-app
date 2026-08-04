package me.tainj.todo.dto.request;

public record CreateTaskRequest(
        String title,
        String description
) {
}
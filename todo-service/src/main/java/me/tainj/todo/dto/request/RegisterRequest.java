package me.tainj.todo.dto.request;

public record RegisterRequest(
        String username,
        String password
) {
}
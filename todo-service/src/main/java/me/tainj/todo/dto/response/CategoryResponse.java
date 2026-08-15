package me.tainj.todo.dto.response;

public record CategoryResponse(
        Long id,
        String name,
        boolean isDefault
) {
}
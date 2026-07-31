package me.tainj.todo.dto.response;

import java.time.OffsetDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        OffsetDateTime createdAt
) {
}
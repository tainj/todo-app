package me.tainj.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.OffsetDateTime;

@JsonPropertyOrder({"id", "title", "description", "completed", "created_at"})
public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        @JsonProperty("created_at") OffsetDateTime createdAt
) {
}
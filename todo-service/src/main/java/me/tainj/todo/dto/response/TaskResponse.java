package me.tainj.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.tainj.todo.model.Task;

import java.time.OffsetDateTime;
import java.util.List;

@JsonPropertyOrder({"id", "title", "description", "completed", "created_at", "deadline", "reminder_offsets", "recurrence"})
public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        OffsetDateTime deadline,
        @JsonProperty("reminder_offsets") List<Integer> reminderOffsets,
        Task.Recurrence recurrence
) {
}
package me.tainj.todo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.tainj.todo.model.Task;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateTaskRequest(
        String title,
        String description,
        @JsonProperty("deadline") OffsetDateTime deadline,
        @JsonProperty("reminder_offsets") List<Integer> reminderOffsets,
        @JsonProperty("recurrence") Task.Recurrence recurrence
) {
}
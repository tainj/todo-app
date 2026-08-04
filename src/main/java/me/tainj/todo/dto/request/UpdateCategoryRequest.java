package me.tainj.todo.dto.request;

public record UpdateCategoryRequest(
    @jakarta.validation.constraints.NotBlank String name
) {
}

package me.tainj.todo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(@NotBlank String name) {}
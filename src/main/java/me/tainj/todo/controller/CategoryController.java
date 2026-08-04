package me.tainj.todo.controller;

import me.tainj.todo.dto.response.CategoryResponse;
import me.tainj.todo.service.CategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAll(Authentication authentication) {
        return categoryService.getAll(getCurrentUsername(authentication));
    }

    private String getCurrentUsername(Authentication authentication) {
        return authentication.getName();
    }
}

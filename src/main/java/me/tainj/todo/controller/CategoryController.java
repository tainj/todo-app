package me.tainj.todo.controller;

import me.tainj.todo.dto.request.CreateCategoryRequest;
import me.tainj.todo.dto.response.CategoryResponse;
import me.tainj.todo.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(Authentication authentication, @RequestBody CreateCategoryRequest createCategoryRequest) {
        return categoryService.create(getCurrentUsername(authentication), createCategoryRequest);
    }

    private String getCurrentUsername(Authentication authentication) {
        return authentication.getName();
    }
}

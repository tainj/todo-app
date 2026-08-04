package me.tainj.todo.service;

import me.tainj.todo.dto.response.CategoryResponse;
import me.tainj.todo.exception.UserNotFoundException;
import me.tainj.todo.model.Category;
import me.tainj.todo.model.User;
import me.tainj.todo.repository.CategoryRepository;
import me.tainj.todo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryResponse> getAll(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Category> categories = new ArrayList<>();
        categories.addAll(categoryRepository.findByUser(user));
        categories.addAll(categoryRepository.findByUserIsNull());

        return categories.stream()
                .map(Category::toResponse)
                .toList();
    }
}

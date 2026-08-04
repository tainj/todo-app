package me.tainj.todo.service;

import me.tainj.todo.dto.request.CreateCategoryRequest;
import me.tainj.todo.dto.request.UpdateCategoryRequest;
import me.tainj.todo.dto.response.CategoryResponse;
import me.tainj.todo.exception.AccessDeniedException;
import me.tainj.todo.exception.CategoryNotFoundException;
import me.tainj.todo.exception.DefaultCategoryException;
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

    public CategoryResponse create(String username, CreateCategoryRequest createCategoryRequest) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));
        Category category = new Category();
        category.setName(createCategoryRequest.name());
        category.setUser(user);
        return categoryRepository.save(category).toResponse();
    }

    public void delete(Long id, String username) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
        if (category.isDefault()) {
            throw new DefaultCategoryException("cannot delete default category");
        }
        if (!category.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("access denied");
        }
        categoryRepository.deleteById(id);
    }

    public CategoryResponse update(Long id, UpdateCategoryRequest updateCategoryRequest, String username) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
        if (category.isDefault()) {
            throw new DefaultCategoryException("cannot update default category");
        }
        if (!category.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("access denied");
        }
        category.setName(updateCategoryRequest.name());
        return categoryRepository.save(category).toResponse();
    }
}

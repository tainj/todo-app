package me.tainj.todo.controller;

import me.tainj.todo.dto.request.CreateTaskRequest;
import me.tainj.todo.dto.request.UpdateTaskRequest;
import me.tainj.todo.dto.response.TaskResponse;
import me.tainj.todo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAll(Authentication authentication) {
        String username = authentication.getName();
        return taskService.getAll(username);
    }

    @PostMapping
    public TaskResponse create(Authentication authentication, @RequestBody CreateTaskRequest task) {
        String username = authentication.getName();
        return taskService.create(task, username);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @RequestBody UpdateTaskRequest task) {
        return taskService.update(id, task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
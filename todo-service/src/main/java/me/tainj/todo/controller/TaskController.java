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
        return taskService.getAll(getCurrentUsername(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(Authentication authentication, @RequestBody CreateTaskRequest task) {
        return taskService.create(task, getCurrentUsername(authentication));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse getTask(Authentication authentication, @PathVariable Long id) {
        return taskService.getTask(id, getCurrentUsername(authentication));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse update(Authentication authentication, @PathVariable Long id, @RequestBody UpdateTaskRequest task) {
        return taskService.update(id, task, getCurrentUsername(authentication));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long id) {
        taskService.delete(id, getCurrentUsername(authentication));
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse complete(Authentication authentication, @PathVariable Long id) {
        return taskService.complete(id, getCurrentUsername(authentication));
    }

    @GetMapping("/history")
    public List<TaskResponse> getHistory(Authentication authentication) {
        return taskService.getHistory(getCurrentUsername(authentication));
    }

    private String getCurrentUsername(Authentication authentication)     {
        return authentication.getName();
    }
}
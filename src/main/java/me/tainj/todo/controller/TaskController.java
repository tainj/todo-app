package me.tainj.todo.controller;

import me.tainj.todo.model.Task;
import me.tainj.todo.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAll() {
        return taskService.getAll();
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }

}
package me.tainj.todo.service;

import me.tainj.todo.model.Task;
import me.tainj.todo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task create(Task task) {
        return this.taskRepository.save(task);
    }

}

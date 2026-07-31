package me.tainj.todo.service;

import me.tainj.todo.exception.TaskNotFoundException;
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

    public Task getTask(Long id) {
        return this.taskRepository.findById(id)
                .orElseThrow();
    }

    public Task update(Long id, Task updated) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found"));
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setCompleted(updated.isCompleted());
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        this.taskRepository.deleteById(id);
    }
}

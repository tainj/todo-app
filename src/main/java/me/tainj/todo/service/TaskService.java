package me.tainj.todo.service;

import me.tainj.todo.dto.request.CreateTaskRequest;
import me.tainj.todo.dto.request.UpdateTaskRequest;
import me.tainj.todo.dto.response.TaskResponse;
import me.tainj.todo.dto.response.UserResponse;
import me.tainj.todo.exception.TaskNotFoundException;
import me.tainj.todo.exception.UserNotFoundException;
import me.tainj.todo.model.Task;
import me.tainj.todo.model.User;
import me.tainj.todo.repository.TaskRepository;
import me.tainj.todo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getAll(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));
        return taskRepository.findByUser(user).stream()
                .map(Task::toResponse)
                .toList();
    }

    public TaskResponse create(CreateTaskRequest request, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setUser(user);
        return taskRepository.save(task).toResponse();
    }

    public TaskResponse getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found")).toResponse();
    }

    public TaskResponse update(Long id, UpdateTaskRequest updated) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found"));
        task.setTitle(updated.title());
        task.setDescription(updated.description());
        task.setCompleted(updated.completed());
        return taskRepository.save(task).toResponse();
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("task not found");
        }
        taskRepository.deleteById(id);
    }
}

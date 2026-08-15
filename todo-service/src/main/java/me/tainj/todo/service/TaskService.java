package me.tainj.todo.service;

import me.tainj.todo.dto.request.CreateTaskRequest;
import me.tainj.todo.dto.request.UpdateTaskRequest;
import me.tainj.todo.dto.response.CategoryResponse;
import me.tainj.todo.dto.response.TaskResponse;
import me.tainj.todo.exception.*;
import me.tainj.todo.model.Category;
import me.tainj.todo.model.Task;
import me.tainj.todo.model.TaskCompletion;
import me.tainj.todo.model.User;
import me.tainj.todo.repository.CategoryRepository;
import me.tainj.todo.repository.TaskCompletionRepository;
import me.tainj.todo.repository.TaskRepository;
import me.tainj.todo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskCompletionRepository taskCompletionRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository, TaskCompletionRepository taskCompletionRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskCompletionRepository = taskCompletionRepository;

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
        task.setDeadline(request.deadline());
        task.setReminderOffsets(request.reminderOffsets());
        task.setRecurrence(request.recurrence() != null ? request.recurrence() : Task.Recurrence.NONE);
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
            task.setCategory(category);
        }
        return taskRepository.save(task).toResponse();
    }

    public TaskResponse getTask(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND));
        if (!task.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        return task.toResponse();
    }

        public TaskResponse update(Long id, UpdateTaskRequest updated, String username) {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND));
            if (!task.getUser().getUsername().equals(username)) {
                throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
            }
            task.setTitle(updated.title());
            task.setDescription(updated.description());
            task.setCompleted(updated.completed());
            return taskRepository.save(task).toResponse();
        }

    public void delete(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND));
        if (!task.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        taskRepository.deleteById(id);
    }

    public TaskResponse complete(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(ErrorMessages.TASK_NOT_FOUND));
        if (!task.getUser().getUsername().equals(username))
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);

        TaskCompletion completion = new TaskCompletion();
        completion.setTask(task);
        completion.setScheduledFor(task.getDeadline());
        taskCompletionRepository.save(completion);

        if (task.getRecurrence() == Task.Recurrence.DAILY) {
            task.setDeadline(task.getDeadline().plusDays(1));
            task.setCompleted(false);
        } else if (task.getRecurrence() == Task.Recurrence.WEEKLY) {
            task.setDeadline(task.getDeadline().plusWeeks(1));
            task.setCompleted(false);
        } else {
            task.setCompleted(true);
        }

        return taskRepository.save(task).toResponse();
    }

    public List<TaskResponse> getHistory(String username) {
        return taskCompletionRepository.findByUsername(username)
                .stream()
                .map(tc -> {
                    Task task = tc.getTask();
                    task.setCompleted(true);
                    return task.toResponse();
                })
                .toList();
    }
}

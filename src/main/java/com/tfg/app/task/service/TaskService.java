package com.tfg.app.task.service;

import com.tfg.app.task.model.Task;
import com.tfg.app.task.repository.TaskRepository;
import com.tfg.app.user.model.User;
import com.tfg.app.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task save(Task task);

    List<Task> findAllByProjectId(Long projectId);

    Optional<Task> findById(Long taskId);

    Task update(Long taskId, Task task);

    Task update(Task task);

    void delete(Long taskId);

    List<Task> findAll();

    Task createTask(Task task);

    Optional<Task> getTask(Long projectId, Long taskId);
    
    Task assignUser(Long taskId, Long userId);
}

@Service
class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task save(Task task) {
        // Manejar la asignación de usuario si viene en el body
        if (task.getAssignedUser() != null && task.getAssignedUser().getId() != null) {
            User user = userRepository.findById(task.getAssignedUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedUser(user);
        }
        return taskRepository.save(task);
    }

    public List<Task> findAllByProjectId(Long projectId) {
        return taskRepository.findAllByProjectId(projectId);
    }

    public Optional<Task> findById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task update(Long taskId, Task task) {
        // Manejar la asignación de usuario si viene en el body
        if (task.getAssignedUser() != null && task.getAssignedUser().getId() != null) {
            User user = userRepository.findById(task.getAssignedUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedUser(user);
        }
        task.setId(taskId);
        return taskRepository.save(task);
    }

    public Task update(Task task) {
        // Extract the ID from the task and call the existing update method
        return update(task.getId(), task);
    }

    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        // Lógica adicional si se requiere, por ejemplo, asegurarse que no haya project asignado.
        task.setProject(null);
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> getTask(Long projectId, Long taskId) {
        // Implement the actual logic to find a task by project ID and task ID
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            // Check if task belongs to the specified project
            if (task.getProject() != null && task.getProject().getId().equals(projectId)) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    @Override
    public Task assignUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        task.setAssignedUser(user);
        return taskRepository.save(task);
    }
}
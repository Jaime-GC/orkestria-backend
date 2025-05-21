package com.tfg.app.task.service;

import com.tfg.app.task.model.Task;
import com.tfg.app.task.repository.TaskRepository;
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
}

@Service
class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findAllByProjectId(Long projectId) {
        return taskRepository.findAllByProjectId(projectId);
    }

    public Optional<Task> findById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task update(Long taskId, Task task) {
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
}
package com.tfg.app.task.service;

import com.tfg.app.task.model.Task;
import com.tfg.app.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
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
}
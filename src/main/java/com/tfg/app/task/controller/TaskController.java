package com.tfg.app.task.controller;

import com.tfg.app.task.model.Task;
import com.tfg.app.task.service.TaskService;
import com.tfg.app.project.model.Project;
import com.tfg.app.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    // Endpoints para todas las tareas (sin filtrar por proyecto)
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAll());
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        return taskService.findById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoints para tareas filtradas por proyecto
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getAllTasksByProject(@PathVariable Long projectId) {
        // Verificar que el proyecto existe
        projectService.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        
        List<Task> tasks = taskService.findAllByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @RequestBody Task task) {
        // Verificar que el proyecto existe
        var projOpt = projectService.findById(projectId);
        if (projOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        task.setProject(projOpt.get());
        Task saved = taskService.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long projectId, @PathVariable Long taskId) {

        // Verificar proyecto
        projectService.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        Task t = taskService.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return ResponseEntity.ok(t);
    }

    @PutMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(
        @PathVariable Long projectId,
        @PathVariable Long taskId,
        @RequestBody Task task) {

        // Verify project exists
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        
        // Verify task exists
        Optional<Task> existingTaskOpt = taskService.findById(taskId);
        if (existingTaskOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        
        // Set correct project and ID
        Project project = projectOpt.get();
        task.setProject(project);
        task.setId(taskId);
        
        // Update task
        Task updated = taskService.update(taskId, task);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        // Verificar que el proyecto existe
        projectService.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Lógica para crear una tarea sin asociar a un proyecto.
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Task> updateTaskWithoutProject(@PathVariable Long taskId, @RequestBody Task task) {
        // Obtener la tarea actual para conservar el valor actual de 'project'
        Task currentTask = taskService.findById(taskId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        // Si el payload no especifica un nuevo project (es null), conservar el actual
        if (task.getProject() == null) {
            task.setProject(currentTask.getProject());
        }
        task.setId(taskId);
        Task updated = taskService.update(taskId, task);
        return ResponseEntity.ok(updated);
    }
}

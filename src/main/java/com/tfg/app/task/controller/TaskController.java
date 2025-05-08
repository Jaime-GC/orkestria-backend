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

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Long projectId) {
        // Verificar que el proyecto existe
        projectService.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        
        List<Task> tasks = taskService.findAllByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @RequestBody Task task) {
        // Verificar que el proyecto existe
        Project project = projectService.findById(projectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        task.setProject(project);
        Task created = taskService.save(task);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long projectId, @PathVariable Long taskId) {

        // Verificar proyecto
        projectService.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        Task t = taskService.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return ResponseEntity.ok(t);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Task task) {

        if(task.getProject() != null && task.getProject().getId() != null && !task.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task project ID does not match the provided project ID");
        }
        Task updated = taskService.update(taskId, task);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        // Verificar que el proyecto existe
        projectService.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}

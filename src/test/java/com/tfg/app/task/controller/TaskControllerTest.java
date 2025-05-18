package com.tfg.app.task.controller;

import com.tfg.app.task.model.Task;
import com.tfg.app.task.service.TaskService;
import com.tfg.app.project.model.Project;
import com.tfg.app.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock TaskService taskService;
    @Mock ProjectService projectService;
    @InjectMocks TaskController controller;

    @Test void getAllTasks_whenProjectExists() {
        Project p = new Project(); p.setId(1L);
        when(projectService.findById(1L)).thenReturn(Optional.of(p));
        List<Task> list = List.of(new Task(), new Task());
        when(taskService.findAllByProjectId(1L)).thenReturn(list);

        ResponseEntity<List<Task>> resp = controller.getAllTasksByProject(1L);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(2, resp.getBody().size());
    }

    @Test void getAllTasks_whenProjectNotFound() {
        when(projectService.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
            () -> controller.getAllTasksByProject(2L));
    }

    @Test void createTask_whenProjectExists() {
        Project p = new Project(); p.setId(3L);
        when(projectService.findById(3L)).thenReturn(Optional.of(p));
        Task t = new Task(); t.setTitle("T1");
        when(taskService.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Task> resp = controller.createTask(3L, t);
        assertEquals(201, resp.getStatusCodeValue());
        assertEquals("T1", resp.getBody().getTitle());
    }

    @Test void getTask_whenExists() {
        Project p = new Project(); p.setId(4L);
        when(projectService.findById(4L)).thenReturn(Optional.of(p));
        Task t = new Task(); t.setId(5L);
        when(taskService.findById(5L)).thenReturn(Optional.of(t));

        ResponseEntity<Task> resp = controller.getTask(4L, 5L);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(5L, resp.getBody().getId());
    }

    @Test void getTask_whenNotFound() {
        Project p = new Project(); p.setId(6L);
        when(projectService.findById(6L)).thenReturn(Optional.of(p));
        when(taskService.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
            () -> controller.getTask(6L, 7L));
    }

    @Test void updateTask_badProjectId() {
        // Setup: Project with ID 8
        Project p = new Project();
        p.setId(8L);
        // Task with different project ID
        Task t = new Task();
        t.setProject(p);
        
        // Mock project service to return empty for project ID 9
        when(projectService.findById(9L)).thenReturn(Optional.empty());
        
        // Expect an exception with 404 status
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> controller.updateTask(9L, 10L, t)
        );
        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Project not found", exception.getReason());
    }

    @Test void deleteTask_whenProjectExists() {
        Project p = new Project(); p.setId(11L);
        when(projectService.findById(11L)).thenReturn(Optional.of(p));
        doNothing().when(taskService).delete(12L);

        ResponseEntity<Void> resp = controller.deleteTask(11L, 12L);
        assertEquals(204, resp.getStatusCodeValue());
    }
}

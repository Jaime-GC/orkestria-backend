package com.tfg.app.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.task.model.Task;
import com.tfg.app.task.service.TaskService;
import com.tfg.app.project.model.Project;
import com.tfg.app.project.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @MockBean
    private TaskService taskService;
    
    @MockBean
    private ProjectService projectService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getAllTasks_shouldReturnAllTasks() throws Exception {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.findAll()).thenReturn(tasks);
        
        // Act & Assert using MockMvc on endpoint "/api/tasks"
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));
    }
    
    @Test
    void getTaskById_whenTaskExists_shouldReturnTask() throws Exception {
        // Arrange
        Long id = 1L;
        Task task = new Task();
        task.setId(id);
        task.setTitle("Test Task");
        
        when(taskService.findById(id)).thenReturn(Optional.of(task));
        
        // Act & Assert via MockMvc on endpoint "/api/tasks/{id}"
        mockMvc.perform(get("/api/tasks/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Test Task")));
    }
    
    @Test
    void getTaskById_whenTaskDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        Long id = 999L;
        when(taskService.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert on endpoint "/api/tasks/{id}"
        mockMvc.perform(get("/api/tasks/" + id))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void createTask_shouldCreateTaskWithoutProject() throws Exception {
        // Arrange
        Task task = new Task();
        task.setId(1L); // Add ID to task 
        task.setTitle("New Task");
        task.setProject(null);
        
        // Use any() matcher instead of exact object
        when(taskService.createTask(any(Task.class))).thenReturn(task);
        
        // Act & Assert on endpoint "/api/tasks"
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("New Task")));
        verify(taskService).createTask(any(Task.class));
    }
    
    @Test
    void deleteTaskById_shouldDeleteTask() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(taskService).delete(id);
        
        // Act & Assert on endpoint "/api/tasks/{id}"
        mockMvc.perform(delete("/api/tasks/" + id))
            .andExpect(status().isNoContent());
        verify(taskService).delete(id);
    }
    
    @Test
    void getAllTasksByProject_shouldReturnTasksForProject() throws Exception {
        // Arrange
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Project Task 1");
        task1.setProject(project);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Project Task 2");
        task2.setProject(project);
        
        List<Task> tasks = Arrays.asList(task1, task2);
        
        when(projectService.findById(projectId)).thenReturn(Optional.of(project));
        when(taskService.findAllByProjectId(projectId)).thenReturn(tasks);
        
        // Act & Assert on endpoint "/api/projects/{projectId}/tasks"
        mockMvc.perform(get("/api/projects/" + projectId + "/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }
    
    @Test
    void getAllTasksByProject_whenProjectNotFound_shouldThrowException() {
        // Arrange
        Long projectId = 999L;
        when(projectService.findById(projectId)).thenReturn(Optional.empty());
        
        // Instantiate a new controller with required beans
        TaskController controller = new TaskController(taskService, projectService);
        
        // Act & Assert (direct call)
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> controller.getAllTasksByProject(projectId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Project not found", ex.getReason());
    }
    
    @Test
    void getTask_success() throws Exception {
        Long projectId = 1L;
        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        
        when(taskService.findById(eq(taskId))).thenReturn(Optional.of(task));
        when(taskService.getTask(eq(projectId), eq(taskId))).thenReturn(Optional.of(task));
        
        Project project = new Project();
        project.setId(projectId);
        when(projectService.findById(eq(projectId))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/projects/" + projectId + "/tasks/" + taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Test Task")));
        verify(taskService, atLeastOnce()).findById(eq(taskId));
    }
    
    @Test
    void getTask_notFound() throws Exception {
        Long projectId = 1L;
        Long taskId = 2L;
        
        when(taskService.findById(eq(taskId))).thenReturn(Optional.empty());
        when(taskService.getTask(eq(projectId), eq(taskId))).thenReturn(Optional.empty());
        
        Project project = new Project();
        project.setId(projectId);
        when(projectService.findById(eq(projectId))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/projects/" + projectId + "/tasks/" + taskId))
            .andExpect(status().isNotFound());
        verify(taskService, atLeastOnce()).findById(eq(taskId));
    }
    
    @Test
    void deleteTask_success() throws Exception {
        Long projectId = 1L;
        Long taskId = 2L;
        
        Project project = new Project();
        project.setId(projectId);
        when(projectService.findById(eq(projectId))).thenReturn(Optional.of(project));
        
        doNothing().when(taskService).delete(taskId);
        
        mockMvc.perform(delete("/api/projects/" + projectId + "/tasks/" + taskId))
            .andExpect(status().isNoContent());
        verify(taskService).delete(eq(taskId));
    }
}

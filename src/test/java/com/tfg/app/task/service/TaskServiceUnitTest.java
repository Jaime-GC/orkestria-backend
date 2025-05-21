package com.tfg.app.task.service;

import com.tfg.app.task.model.Task;
import com.tfg.app.task.repository.TaskRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void save_shouldDelegateToRepository() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);
        Task result = taskService.save(task);
        assertEquals(task, result);
        verify(taskRepository).save(task);
    }

    @Test
    void findAllByProjectId_shouldReturnTasks() {
        List<Task> mockList = List.of(new Task(), new Task());
        when(taskRepository.findAllByProjectId(1L)).thenReturn(mockList);
        List<Task> result = taskService.findAllByProjectId(1L);
        assertEquals(2, result.size());
        verify(taskRepository).findAllByProjectId(1L);
    }

    @Test
    void findById_whenExists() {
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Optional<Task> result = taskService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(task, result.get());
    }

    @Test
    void findById_whenNotExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(taskService.findById(1L).isEmpty());
    }

    @Test
    void update_shouldSaveWithId() {
        Task task = new Task();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        Task result = taskService.update(42L, task);
        assertEquals(42L, result.getId());
        verify(taskRepository).save(task);
    }

    @Test
    void delete_shouldInvokeRepository() {
        taskService.delete(7L);
        verify(taskRepository).deleteById(7L);
    }
    
    @Test
    void getTask_shouldReturnTaskWithMatchingProjectId() {
        // Arrange
        Long projectId = 1L;
        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        
        // Create a task with matching project
        com.tfg.app.project.model.Project project = new com.tfg.app.project.model.Project();
        project.setId(projectId);
        task.setProject(project);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        // Act
        Optional<Task> result = taskService.getTask(projectId, taskId);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get().getId());
    }
    
    @Test
    void getTask_shouldReturnEmptyWhenProjectIdDoesNotMatch() {
        // Arrange
        Long projectId = 1L;
        Long differentProjectId = 5L;
        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        
        // Create a task with different project ID
        com.tfg.app.project.model.Project project = new com.tfg.app.project.model.Project();
        project.setId(differentProjectId); // Different from the one we'll search with
        task.setProject(project);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        
        // Act
        Optional<Task> result = taskService.getTask(projectId, taskId);
        
        // Assert
        assertFalse(result.isPresent());
    }
}

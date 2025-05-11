package com.tfg.app.project.service;

import com.tfg.app.project.model.Project;
import com.tfg.app.project.repository.ProjectRepository;
import com.tfg.app.project.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceUnitTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void save_shouldDelegateToRepository() {
        Project project = new Project();
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.save(project);

        assertEquals(project, result);
        verify(projectRepository).save(project);
    }

    @Test
    void findAll_shouldReturnProjects() {
        List<Project> mockList = List.of(new Project(), new Project());
        when(projectRepository.findAll()).thenReturn(mockList);

        List<Project> result = projectService.findAll();

        assertEquals(2, result.size());
        verify(projectRepository).findAll();
    }

    @Test
    void findById_whenExists() {
        Project project = new Project();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<Project> result = projectService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(project, result.get());
    }

    @Test
    void findById_whenNotExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertTrue(projectService.findById(1L).isEmpty());
    }

    @Test
    void update_shouldSaveWithId() {
        Project project = new Project();
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.update(5L, project);

        assertEquals(5L, result.getId());
        verify(projectRepository).save(project);
    }

    @Test
    void delete_shouldInvokeRepository() {
        projectService.delete(9L);

        verify(projectRepository).deleteById(9L);
    }
}

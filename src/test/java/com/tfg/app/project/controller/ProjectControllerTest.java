package com.tfg.app.project.controller;

import com.tfg.app.project.model.Project;
import com.tfg.app.project.service.ProjectService;
import com.tfg.app.project.controller.ProjectController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock ProjectService projectService;
    @InjectMocks ProjectController controller;

    @Test void findAll_returnsList() {
        List<Project> list = List.of(new Project(), new Project());
        when(projectService.findAll()).thenReturn(list);
        ResponseEntity<List<Project>> resp = controller.findAll();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(2, resp.getBody().size());
    }

    @Test void save_returnsCreated() {
        Project p = new Project(); p.setId(1L);
        when(projectService.save(any())).thenReturn(p);
        ResponseEntity<Project> resp = controller.save(new Project());
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(1L, resp.getBody().getId());
    }

    @Test void findById_found() {
        Project p = new Project(); p.setId(2L);
        when(projectService.findById(2L)).thenReturn(Optional.of(p));
        ResponseEntity<Project> resp = controller.findById(2L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(2L, resp.getBody().getId());
    }

    @Test void findById_notFound() {
        when(projectService.findById(3L)).thenReturn(Optional.empty());
        ResponseEntity<Project> resp = controller.findById(3L);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test void update_returnsOk() {
        Project existing = new Project(); existing.setId(4L);
        Project updated = new Project(); updated.setId(4L);
        when(projectService.update(4L, existing)).thenReturn(updated);
        ResponseEntity<Project> resp = controller.update(4L, existing);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(4L, resp.getBody().getId());
    }

    @Test void delete_returnsNoContent() {
        ResponseEntity<Void> resp = controller.delete(5L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(projectService).delete(5L);
    }
}

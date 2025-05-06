package com.tfg.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;

import com.tfg.app.entity.Project;
import com.tfg.app.entity.Task;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrkestriaBackendApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void createAndGetProject() {
        Project project = Project.builder()
            .name("Integration Project")
            .description("Test desc")
            .startDate(LocalDate.now())
            .status(Project.ProjectStatus.PLANNED)
            .build();

        ResponseEntity<Project> postResponse =
            restTemplate.postForEntity("/api/projects", project, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        Long id = postResponse.getBody().getId();

        ResponseEntity<Project> getResponse =
            restTemplate.getForEntity("/api/projects/" + id, Project.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Project", getResponse.getBody().getName());
    }

    @Test
    void createAndGetTask() {
        // primero, crea un proyecto
        Project project = Project.builder()
            .name("Task Project")
            .description("Test desc")
            .startDate(LocalDate.now())
            .status(Project.ProjectStatus.PLANNED)
            .build();
        ResponseEntity<Project> projectResponse = restTemplate.postForEntity("/api/projects", project, Project.class);
        assertNotNull(projectResponse.getBody(), "Project response body is null");
        Long projectId = projectResponse.getBody().getId();

        Task task = new Task();
        task.setTitle("Integration Task");
        task.setDescription("Task desc");
        task.setPriority(Task.Priority.MEDIUM);
        task.setType(Task.Type.OTHER);
        task.setStatus(Task.Status.TODO);

        ResponseEntity<Task> postTask =
            restTemplate.postForEntity("/api/projects/" + projectId + "/tasks", task, Task.class);
        assertEquals(HttpStatus.CREATED, postTask.getStatusCode());
        Long taskId = postTask.getBody().getId();

        ResponseEntity<Task[]> getTasks =
            restTemplate.getForEntity("/api/projects/" + projectId + "/tasks", Task[].class);
        assertEquals(HttpStatus.OK, getTasks.getStatusCode());
        assertTrue(Arrays.stream(getTasks.getBody())
                       .anyMatch(t -> t.getId().equals(taskId)));
    }
}
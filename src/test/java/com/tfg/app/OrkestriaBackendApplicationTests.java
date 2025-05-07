package com.tfg.app;

import com.tfg.app.project.model.Project;
import com.tfg.app.task.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrkestriaBackendApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // simplemente arranca el contexto
    }

    @Test
    void createProjectAndTaskFlow() {
        // 1) Crear un proyecto
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project for integration test");
        project.setStartDate(LocalDate.now());
        project.setStatus(Project.ProjectStatus.PLANNED);

        ResponseEntity<Project> projectResponse =
            restTemplate.postForEntity("/api/projects", project, Project.class);
        assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        Project createdProject = projectResponse.getBody();
        assertNotNull(createdProject, "Project response body is null");
        Long projectId = createdProject.getId();
        assertNotNull(projectId, "Project ID should not be null");

        // 2) Crear una tarea dentro de ese proyecto
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Task for integration test");
        task.setPriority(Task.Priority.MEDIUM);
        task.setType(Task.Type.OTHER);
        task.setStatus(Task.Status.TODO);

        ResponseEntity<Task> postTask =
            restTemplate.postForEntity(
                "/api/projects/" + projectId + "/tasks",
                task,
                Task.class
            );
        assertEquals(HttpStatus.CREATED, postTask.getStatusCode());
        Task createdTask = postTask.getBody();
        assertNotNull(createdTask, "Task response body is null");
        Long taskId = createdTask.getId();
        assertNotNull(taskId, "Task ID should not be null");

        // 3) Recuperar la lista de tareas y comprobar que incluye la creada
        ResponseEntity<Task[]> getTasks =
            restTemplate.getForEntity(
                "/api/projects/" + projectId + "/tasks",
                Task[].class
            );
        assertEquals(HttpStatus.OK, getTasks.getStatusCode());
        Task[] tasks = getTasks.getBody();
        assertNotNull(tasks, "Task list should not be null");
        assertTrue(
            Arrays.stream(tasks).anyMatch(t -> t.getId().equals(taskId)),
            "Created task must appear in task list"
        );
    }
}

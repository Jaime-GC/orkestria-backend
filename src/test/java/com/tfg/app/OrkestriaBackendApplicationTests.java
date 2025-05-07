package com.tfg.app;

import com.tfg.app.OrkestriaBackendApplication;   // importa la clase principal
import com.tfg.app.project.model.Project;
import com.tfg.app.task.model.Task;
import com.tfg.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = OrkestriaBackendApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class
})
class OrkestriaBackendApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createProjectAndTaskFlow() {
        // 1) Crear proyecto
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project for integration test");
        project.setStartDate(LocalDate.now());
        project.setStatus(Project.ProjectStatus.PLANNED);

        ResponseEntity<Project> projectResponse =
            restTemplate.postForEntity("/api/projects", project, Project.class);
        assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        assertNotNull(projectResponse.getBody(), "Project response body is null");
        Long projectId = projectResponse.getBody().getId();
        assertNotNull(projectId, "Project ID should not be null");

        // 2) Crear tarea
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
        assertNotNull(postTask.getBody(), "Task response body is null");
        Long taskId = postTask.getBody().getId();
        assertNotNull(taskId, "Task ID should not be null");

        // 3) Listar tareas
        ResponseEntity<Task[]> getTasks =
            restTemplate.getForEntity(
                "/api/projects/" + projectId + "/tasks",
                Task[].class
            );
        assertEquals(HttpStatus.OK, getTasks.getStatusCode());
        assertNotNull(getTasks.getBody(), "Task list should not be null");
        assertTrue(
            Arrays.stream(getTasks.getBody())
                  .anyMatch(t -> t.getId().equals(taskId)),
            "Created task must appear in task list"
        );
    }

    @Test
    void userCrudFlow() {
        // CREATE
        var u = new User();
        u.setUsername("jane");
        u.setEmail("jane@x.com");
        u.setPassword("pwd");
        ResponseEntity<User> post = restTemplate.postForEntity(
            "/api/users", u, User.class);
        assertEquals(HttpStatus.CREATED, post.getStatusCode());
        Long id = post.getBody().getId();

        // LIST contains new
        User[] arr = restTemplate.getForEntity(
            "/api/users", User[].class).getBody();
        assertTrue(Arrays.stream(arr)
                   .anyMatch(x -> x.getId().equals(id)));

        // UPDATE
        u.setEmail("jane2@x.com");
        HttpEntity<User> putReq = new HttpEntity<>(u);
        ResponseEntity<User> put = restTemplate.exchange(
            "/api/users/"+id, HttpMethod.PUT, putReq, User.class);
        assertEquals(HttpStatus.OK, put.getStatusCode());
        assertEquals("jane2@x.com", put.getBody().getEmail());

        // GET by id
        ResponseEntity<User> get = restTemplate.getForEntity(
            "/api/users/"+id, User.class);
        assertEquals(HttpStatus.OK, get.getStatusCode());
        assertEquals("jane2@x.com", get.getBody().getEmail());

        // DELETE
        ResponseEntity<Void> del = restTemplate.exchange(
            "/api/users/"+id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, del.getStatusCode());

        // GET missing → 404
        assertEquals(HttpStatus.NOT_FOUND,
            restTemplate.getForEntity("/api/users/"+id, User.class)
                        .getStatusCode());
    }
}

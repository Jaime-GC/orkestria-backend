package com.tfg.app;

import com.tfg.app.project.model.Project;
import com.tfg.app.task.model.Task;
import com.tfg.app.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        // Desactiva Flyway y fuerza a Hibernate a recrear el esquema en memoria
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    }
)
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
        Long projectId = projectResponse.getBody().getId();

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
        Long taskId = postTask.getBody().getId();

        // 3) Listar tareas y comprobar que aparece la creada
        ResponseEntity<Task[]> getTasks =
            restTemplate.getForEntity(
                "/api/projects/" + projectId + "/tasks",
                Task[].class
            );
        assertEquals(HttpStatus.OK, getTasks.getStatusCode());
        assertTrue(
            Arrays.stream(getTasks.getBody())
                  .anyMatch(t -> t.getId().equals(taskId))
        );
    }

    @Test
    void userCrudFlow() {
        // 1) Crear usuario con rol CLIENT
        User user = User.builder()
                        .username("alice")
                        .email("alice@example.com")
                        .role(User.Role.CLIENT)
                        .build();

        ResponseEntity<User> createRes =
            restTemplate.postForEntity("/api/users", user, User.class);
        assertEquals(HttpStatus.CREATED, createRes.getStatusCode());
        User created = createRes.getBody();
        assertNotNull(created);
        Long userId = created.getId();

        // 2) Listar usuarios y comprobar que esté alice
        ResponseEntity<User[]> listRes =
            restTemplate.getForEntity("/api/users", User[].class);
        assertEquals(HttpStatus.OK, listRes.getStatusCode());
        assertTrue(
            Arrays.stream(listRes.getBody())
                  .anyMatch(u -> u.getId().equals(userId)),
            "El usuario creado debe aparecer en la lista"
        );

        // 3) Actualizar email
        created.setEmail("alice2@example.com");
        HttpEntity<User> updateReq = new HttpEntity<>(created);
        ResponseEntity<User> updateRes =
            restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.PUT,
                updateReq,
                User.class
            );
        assertEquals(HttpStatus.OK, updateRes.getStatusCode());
        assertEquals("alice2@example.com", updateRes.getBody().getEmail());

        // 4) Obtener por ID y verificar email
        ResponseEntity<User> getRes =
            restTemplate.getForEntity("/api/users/" + userId, User.class);
        assertEquals(HttpStatus.OK, getRes.getStatusCode());
        assertEquals("alice2@example.com", getRes.getBody().getEmail());

        // 5) Borrar usuario
        restTemplate.delete("/api/users/" + userId);

        // 6) Verificar 404 NOT_FOUND al consultar de nuevo
        ResponseEntity<User> afterDelete =
            restTemplate.getForEntity("/api/users/" + userId, User.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDelete.getStatusCode());
    }

    @Test
    void userRoleFlow() {
        // 1) Crear usuario con rol CLIENT
        User user = User.builder()
                        .username("bob")
                        .email("bob@example.com")
                        .role(User.Role.CLIENT)
                        .build();

        ResponseEntity<User> createRes =
            restTemplate.postForEntity("/api/users", user, User.class);
        assertEquals(HttpStatus.CREATED, createRes.getStatusCode());
        User created = createRes.getBody();
        assertNotNull(created);
        Long userId = created.getId();
        assertEquals(User.Role.CLIENT, created.getRole());

        // 2) Cambiar rol a ADMIN mediante PUT /api/users/{id}
        created.setRole(User.Role.ADMIN);
        HttpEntity<User> roleUpdateReq = new HttpEntity<>(created);
        ResponseEntity<User> roleUpdateRes =
            restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.PUT,
                roleUpdateReq,
                User.class
            );
        assertEquals(HttpStatus.OK, roleUpdateRes.getStatusCode());
        assertEquals(User.Role.ADMIN, roleUpdateRes.getBody().getRole());

        // 3) Verificar GET /api/users/{id} devuelve rol ADMIN
        ResponseEntity<User> getRes =
            restTemplate.getForEntity("/api/users/" + userId, User.class);
        assertEquals(HttpStatus.OK, getRes.getStatusCode());
        assertEquals(User.Role.ADMIN, getRes.getBody().getRole());
    }
}

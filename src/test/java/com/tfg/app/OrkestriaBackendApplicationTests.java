package com.tfg.app;

import com.tfg.app.project.model.Project;
import com.tfg.app.resource.model.*;
import com.tfg.app.task.model.Task;
import com.tfg.app.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        // Disable Flyway and force Hibernate to recreate the schema in memory
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    }
)
class OrkestriaBackendApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createProjectAndTaskFlow() {
        // 1) Create project
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project for integration test");
        project.setStartDate(LocalDate.now());
        project.setStatus(Project.ProjectStatus.PLANNED);

        ResponseEntity<Project> projectResponse =
            restTemplate.postForEntity("/api/projects", project, Project.class);
        assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        Long projectId = projectResponse.getBody().getId();

        // 2) Create task
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

        // 3) List tasks and verify the created one appears
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
        // 1) Create user with role CLIENT
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

        // 2) List users and verify that alice is present
        ResponseEntity<User[]> listRes =
            restTemplate.getForEntity("/api/users", User[].class);
        assertEquals(HttpStatus.OK, listRes.getStatusCode());
        assertTrue(
            Arrays.stream(listRes.getBody())
                  .anyMatch(u -> u.getId().equals(userId)),
            "The created user must appear in the list"
        );

        // 3) Update email
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

        // 4) Get by ID and verify email
        ResponseEntity<User> getRes =
            restTemplate.getForEntity("/api/users/" + userId, User.class);
        assertEquals(HttpStatus.OK, getRes.getStatusCode());
        assertEquals("alice2@example.com", getRes.getBody().getEmail());

        // 5) Delete user
        restTemplate.delete("/api/users/" + userId);

        // 6) Verify 404 NOT_FOUND when querying again
        ResponseEntity<User> afterDelete =
            restTemplate.getForEntity("/api/users/" + userId, User.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDelete.getStatusCode());
    }

    @Test
    void userRoleFlow() {
        // 1) Create user with role CLIENT
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

        // 2) Change role to EMPLOYEE via PUT /api/users/{id}
        created.setRole(User.Role.EMPLOYEE);
        HttpEntity<User> roleUpdateReq = new HttpEntity<>(created);
        ResponseEntity<User> roleUpdateRes =
            restTemplate.exchange(
            "/api/users/" + userId,
            HttpMethod.PUT,
            roleUpdateReq,
            User.class
            );
        assertEquals(HttpStatus.OK, roleUpdateRes.getStatusCode());
        assertEquals(User.Role.EMPLOYEE, roleUpdateRes.getBody().getRole());

        // 3) Verify GET /api/users/{id} returns role EMPLOYEE
        ResponseEntity<User> getRes =
            restTemplate.getForEntity("/api/users/" + userId, User.class);
        assertEquals(HttpStatus.OK, getRes.getStatusCode());
        assertEquals(User.Role.EMPLOYEE, getRes.getBody().getRole());
    }

    @Test
    void resourceModuleFlow() {
        // 1) Create parent group
        Map<String,Object> parent = new LinkedHashMap<>();
        parent.put("name", "Rooms");
        ResponseEntity<Map> pRes = restTemplate.postForEntity(
            "/api/resource-groups",
            new HttpEntity<>(parent, jsonHeaders()),
            Map.class
        );
        assertEquals(HttpStatus.CREATED, pRes.getStatusCode());
        Long pId = ((Number)pRes.getBody().get("id")).longValue();

        // 2) Create sub-group referencing the parent (nested in "parent")
        Map<String,Object> child = new LinkedHashMap<>();
        child.put("name", "VIP Room");
        // nest the parent object with its id, not parentId
        child.put("parent", Map.of("id", pId));
        ResponseEntity<Map> cRes = restTemplate.postForEntity(
            "/api/resource-groups",
            new HttpEntity<>(child, jsonHeaders()),
            Map.class
        );
        assertEquals(HttpStatus.CREATED, cRes.getStatusCode());
        Long cId = ((Number)cRes.getBody().get("id")).longValue();
        // verify the nested parent comes with its id
        @SuppressWarnings("unchecked")
        Map<String,Object> returnedParent = (Map<String, Object>) cRes.getBody().get("parent");
        assertEquals(pId, ((Number)returnedParent.get("id")).longValue());

        // 3) Create reservable resource (using resource-groups but with isReservable=true)
        Map<String,Object> reservable = new LinkedHashMap<>();
        reservable.put("name", "Room A");
        reservable.put("parent", Map.of("id", pId));
        reservable.put("isReservable", true);
        ResponseEntity<Map> iRes = restTemplate.postForEntity(
            "/api/resource-groups",
            new HttpEntity<>(reservable, jsonHeaders()),
            Map.class
        );
        assertEquals(HttpStatus.CREATED, iRes.getStatusCode());
        Long iId = ((Number)iRes.getBody().get("id")).longValue();

        // 4) Valid reservation → 201 CREATED
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end   = start.plusHours(1);
        Map<String,Object> r1 = new LinkedHashMap<>();
        r1.put("startDateTime", start.toString());
        r1.put("endDateTime",   end.toString());
        r1.put("reservedBy",    "alice");
        ResponseEntity<Map> ok = restTemplate.postForEntity(
            "/api/resource-groups/" + iId + "/reservations",
            new HttpEntity<>(r1, jsonHeaders()),
            Map.class
        );
        assertEquals(HttpStatus.CREATED, ok.getStatusCode());
        Long resId = ((Number)ok.getBody().get("id")).longValue();

        // 5) Overlapping reservation → 400 BAD_REQUEST + error
        ResponseEntity<Map> bad = restTemplate.postForEntity(
            "/api/resource-groups/" + iId + "/reservations",
            new HttpEntity<>(r1, jsonHeaders()),
            Map.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
        assertTrue(bad.getBody().containsKey("error"));

        // 6) Availability → only 1
        ResponseEntity<List> avail = restTemplate.exchange(
            "/api/resource-groups/" + iId + "/availability?from=" + start + "&to=" + end,
            HttpMethod.GET,
            new HttpEntity<>(jsonHeaders()),
            List.class
        );
        assertEquals(1, avail.getBody().size());

        // 7) Update reservation
        Map<String,Object> upd = new LinkedHashMap<>(ok.getBody());
        upd.put("reservedBy", "bob");
        ResponseEntity<Map> uRes = restTemplate.exchange(
            "/api/resource-groups/" + iId + "/reservations/" + resId,
            HttpMethod.PUT,
            new HttpEntity<>(upd, jsonHeaders()),
            Map.class
        );
        assertEquals(HttpStatus.OK, uRes.getStatusCode());
        assertEquals("bob", uRes.getBody().get("reservedBy"));

        // 8) Delete reservation and list
        restTemplate.exchange(
            "/api/resource-groups/" + iId + "/reservations/" + resId,
            HttpMethod.DELETE,
            new HttpEntity<>(jsonHeaders()),
            Void.class
        );
        ResponseEntity<List> afterDel = restTemplate.exchange(
            "/api/resource-groups/" + iId + "/reservations",
            HttpMethod.GET,
            new HttpEntity<>(jsonHeaders()),
            List.class
        );
        assertTrue(afterDel.getBody().isEmpty());

        // 9) Delete resource and verify it no longer appears
        restTemplate.exchange(
            "/api/resource-groups/" + iId,
            HttpMethod.DELETE,
            new HttpEntity<>(jsonHeaders()),
            Void.class
        );
        ResponseEntity<List> items = restTemplate.exchange(
            "/api/resource-groups",
            HttpMethod.GET,
            new HttpEntity<>(jsonHeaders()),
            List.class
        );
        assertTrue(((List<?>)items.getBody()).stream()
                    .noneMatch(x -> ((Map<?,?>)x).get("id").equals(iId)));
    }

    // Helper to set Content-Type: application/json
    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}

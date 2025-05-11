# Orkestria Backend

## Description
Orkestria Backend is a monolithic Spring Boot project to manage projects, tasks and users. It provides REST endpoints for CRUD operations on Projects, Tasks and Users, backed by PostgreSQL.

## Technologies Used
- Java 21
- Spring Boot
- Spring Data JPA
- Lombok
- PostgreSQL
- Flyway for DB migrations

## Project Structure
```
orkestria-backend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── tfg
│   │   │           └── app
│   │   │               ├── OrkestriaBackendApplication.java
│   │   │               ├── project
│   │   │               │   ├── model
│   │   │               │   │   └── Project.java
│   │   │               │   ├── repository
│   │   │               │   │   └── ProjectRepository.java
│   │   │               │   ├── service
│   │   │               │   │   └── ProjectService.java
│   │   │               │   └── controller
│   │   │               │       └── ProjectController.java
│   │   │               ├── task
│   │   │               │   ├── model
│   │   │               │   │   └── Task.java
│   │   │               │   ├── repository
│   │   │               │   │   └── TaskRepository.java
│   │   │               │   ├── service
│   │   │               │   │   └── TaskService.java
│   │   │               │   └── controller
│   │   │               │       └── TaskController.java
│   │   │               └── user
│   │   │                   ├── model
│   │   │                   │   └── User.java
│   │   │                   ├── repository
│   │   │                   │   └── UserRepository.java
│   │   │                   ├── service
│   │   │                   │   └── UserService.java
│   │   │                   └── controller
│   │   │                       └── UserController.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── db
│   │           └── migration
│   │               └── V1__init.sql
│   └── test
│       └── java
│           └── com
│               └── tfg
│                   └── app
│                       ├── OrkestriaBackendApplicationTests.java
│                       ├── project
│                       │   └── controller
│                       │       └── ProjectControllerTest.java
│                       ├── project
│                       │   └── service
│                       │       └── ProjectServiceUnitTest.java
│                       ├── task
│                       │   └── controller
│                       │       └── TaskControllerTest.java
│                       ├── task
│                       │   └── service
│                       │       └── TaskServiceUnitTest.java
│                       └── user
│                           ├── controller
│                           │   └── UserControllerTest.java
│                           └── service
│                               └── UserServiceTest.java
├── pom.xml
└── README.md
```

## Configuration
1. **Database**  
   Ensure PostgreSQL is installed and running. Create a database for this project.
2. **application.properties**  
   Configure your database connection in `src/main/resources/application.properties`.
3. **Migrations**  
   Flyway will automatically apply the SQL scripts in `db/migration` on startup.

## Running the Application
From the project root, run:
```
mvn spring-boot:run
```

## REST API

### Projects
- `GET /api/projects`: Retrieve all projects.
- `POST /api/projects`: Create a new project.
- `GET /api/projects/{id}`: Retrieve a project by ID.
- `PUT /api/projects/{id}`: Update an existing project.
- `DELETE /api/projects/{id}`: Delete a project.

### Tasks
- `GET /api/projects/{projectId}/tasks`: Retrieve all tasks for a project.
- `POST /api/projects/{projectId}/tasks`: Create a new task in a project.
- `GET /api/projects/{projectId}/tasks/{taskId}`: Retrieve a task by ID.
- `PUT /api/projects/{projectId}/tasks/{taskId}`: Update an existing task.
- `DELETE /api/projects/{projectId}/tasks/{taskId}`: Delete a task.

### Users
- Each user has a single `role` chosen from {ADMIN, MANAGER, EMPLOYEE, CLIENT}.
- `GET /api/users`: Retrieve all users.
- `POST /api/users`: Create a new user.
- `GET /api/users/{id}`: Retrieve a user by ID.
- `PUT /api/users/{id}`: Update an existing user.
- `DELETE /api/users/{id}`: Delete a user.
- `PUT /api/users/{id}/role`: Update the user’s role.

## Database Migrations
Flyway scripts in `src/main/resources/db/migration`:
```
V1__init.sql  # creates tables projects, tasks, users (with 'role' column)
```

## Testing
- Unit tests under `src/test/java/com/tfg/app/...`  
- Integration tests in `OrkestriaBackendApplicationTests.java`  

Run all tests:
```
mvn test
```

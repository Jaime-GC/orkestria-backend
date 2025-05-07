# Orkestria Backend

## Description
Orkestria Backend is a monolithic Spring Boot project designed to manage projects and tasks. It provides a REST API for creating, reading, updating, and deleting (CRUD) projects and tasks using a PostgreSQL database.

## Technologies Used
- **Java 21**: Programming language.
- **Spring Boot**: Framework for building Java applications.
- **Spring Data JPA**: For data persistence.
- **Lombok**: To reduce boilerplate code.
- **PostgreSQL**: Database system.
- **Flyway**: For database migration management.

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
│   │   │               ├── entity
│   │   │               │   ├── Project.java
│   │   │               │   └── Task.java
│   │   │               ├── repository
│   │   │               │   ├── ProjectRepository.java
│   │   │               │   └── TaskRepository.java
│   │   │               ├── service
│   │   │               │   ├── ProjectService.java
│   │   │               │   └── TaskService.java
│   │   │               └── controller
│   │   │                   ├── ProjectController.java
│   │   │                   └── TaskController.java
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
│                       └── OrkestriaBackendApplicationTests.java
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

## Testing
Unit and integration tests are located under `src/test/java/com/tfg/app`.  
To run tests:
```
mvn test
```

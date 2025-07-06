# Orkestria Backend

## Description
Orkestria Backend is a Spring Boot application for managing projects, tasks, users, resources, and scheduling. It provides a comprehensive REST API for organizational resource management, backed by PostgreSQL.

## Technologies Used
- Java 21
- Spring Boot 3
- Spring Data JPA
- Lombok
- PostgreSQL
- Flyway for DB migrations
- JaCoCo for code coverage

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
│   │   │               ├── config
│   │   │               ├── project
│   │   │               │   ├── model
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   └── controller
│   │   │               ├── task
│   │   │               │   ├── model
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   └── controller
│   │   │               ├── resource
│   │   │               │   ├── model
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   └── controller
│   │   │               └── user
│   │   │                   ├── model
│   │   │                   ├── repository
│   │   │                   ├── service
│   │   │                   └── controller
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── db
│   │           └── migration
│   │               ├── V1__init.sql
│   │               ├── V2__add_employee_schedules.sql
│   │               ├── V3__merge_resource_items_to_groups.sql
│   │               ├── V4__allow_null_project_in_tasks.sql
│   │               └── V5__add_title_to_employee_schedules.sql
│   └── test
│       └── java
│           └── com
│               └── tfg
│                   └── app
│                       ├── OrkestriaBackendApplicationTests.java
│                       ├── project
│                       ├── task
│                       ├── resource
│                       └── user
├── pom.xml
└── README.md
```

## Configuration
1. **Database**  
   PostgreSQL database is required. Create a database named `orkestria_db`.
   
2. **application.properties**  
   Configure database connection in application.properties or use the default development settings in `application-dev.properties`.
   
3. **Migrations**  
   Flyway automatically applies database migrations on startup.

## Running the Application
From the project root directory, run:
```
mvn spring-boot:run
```

Access the API at http://localhost:8080/api and Swagger documentation at http://localhost:8080/swagger-ui.html

## REST API

### Projects
- `GET /api/projects`: Retrieve all projects
- `POST /api/projects`: Create a new project
- `GET /api/projects/{id}`: Retrieve project by ID
- `PUT /api/projects/{id}`: Update an existing project
- `DELETE /api/projects/{id}`: Delete a project

### Tasks
- `GET /api/tasks`: Retrieve all tasks
- `POST /api/tasks`: Create a new task (no project required)
- `GET /api/tasks/{id}`: Get task by ID
- `PUT /api/tasks/{id}`: Update a task
- `DELETE /api/tasks/{id}`: Delete a task
- `PUT /api/tasks/{taskId}/assign-user/{userId}`: Assign a user to a task
- `GET /api/projects/{projectId}/tasks`: Get all tasks for a project
- `POST /api/projects/{projectId}/tasks`: Create a task in a project
- `GET /api/projects/{projectId}/tasks/{taskId}`: Get task by ID within a project
- `PUT /api/projects/{projectId}/tasks/{taskId}`: Update task within a project
- `DELETE /api/projects/{projectId}/tasks/{taskId}`: Delete task from a project

### Users
- `GET /api/users`: Retrieve all users
- `POST /api/users`: Create a new user
- `GET /api/users/{id}`: Get user by ID
- `PUT /api/users/{id}`: Update user
- `DELETE /api/users/{id}`: Delete user
- `PUT /api/users/{id}/role`: Assign a role to a user

### Resource Groups
- `GET /api/resource-groups`: List all resource groups
- `POST /api/resource-groups`: Create a resource group
- `GET /api/resource-groups/{id}`: Get resource group by ID
- `PUT /api/resource-groups/{id}`: Update a resource group
- `DELETE /api/resource-groups/{id}`: Delete a resource group
- `GET /api/resource-groups/{id}/children`: Get child resource groups

### Employee Schedules
- `GET /api/employee-schedules`: List all employee schedules
- `POST /api/employee-schedules`: Create an employee schedule
- `GET /api/employee-schedules/{id}`: Get schedule by ID
- `PUT /api/employee-schedules/{id}`: Update a schedule
- `DELETE /api/employee-schedules/{id}`: Delete a schedule

### Reservations
- `GET /api/reservations`: List all space reservations
- `PUT /api/reservations/{resId}`: Update a reservation
- `DELETE /api/reservations/{resId}`: Delete a reservation
- `GET /api/resource-groups/{id}/reservations`: List reservations for a resource group
- `POST /api/resource-groups/{id}/reservations`: Create a reservation for a resource group
- `GET /api/resource-groups/{id}/availability`: Check resource availability
- `GET /api/resource-groups/{groupId}/reservations/{resId}`: Get specific reservation
- `PUT /api/resource-groups/{groupId}/reservations/{resId}`: Update specific reservation
- `DELETE /api/resource-groups/{groupId}/reservations/{resId}`: Delete specific reservation

## Database Schema
The database includes tables for:
- `projects`: Project management
- `tasks`: Task management (can be associated with projects or standalone, and can be assigned to users)
- `users`: User management
- `resource_groups`: Hierarchical resource organization
- `employee_schedules`: Employee scheduling with title, start and end times
- `space_reservations`: Resource reservation management

## Testing
- Unit tests under `src/test/java/com/tfg/app/...`
- JaCoCo for test coverage analysis

Run tests and generate coverage report:
```
mvn clean test jacoco:report
```


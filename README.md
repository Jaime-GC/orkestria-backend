# Orkestria Backend

## Descripción
Orkestria Backend es un proyecto monolítico desarrollado en Spring Boot que permite gestionar proyectos y tareas. Este backend proporciona una API REST para la creación, lectura, actualización y eliminación (CRUD) de proyectos y tareas, utilizando una base de datos PostgreSQL.

## Tecnologías Utilizadas
- **Java 21**: Lenguaje de programación utilizado para el desarrollo.
- **Spring Boot**: Framework para la creación de aplicaciones Java.
- **Spring Data JPA**: Para la gestión de la persistencia de datos.
- **Lombok**: Para reducir el boilerplate de código.
- **PostgreSQL**: Sistema de gestión de bases de datos utilizado.
- **Flyway**: Para la gestión de migraciones de base de datos.

## Estructura del Proyecto
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
│   │   │               │   ├── TaskService.java
│   │   │               │   └── impl
│   │   │               │       ├── ProjectServiceImpl.java
│   │   │               │       └── TaskServiceImpl.java
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

## Configuración
1. **Base de Datos**: Asegúrate de tener PostgreSQL instalado y en funcionamiento. Crea una base de datos para el proyecto.
2. **application.properties**: Configura la conexión a la base de datos en el archivo `src/main/resources/application.properties` con los detalles de tu base de datos PostgreSQL.
3. **Migraciones**: Flyway se encargará de crear las tablas necesarias al iniciar la aplicación. Asegúrate de que el archivo `V1__init.sql` esté correctamente configurado.

## Ejecución
Para ejecutar la aplicación, utiliza el siguiente comando en la raíz del proyecto:
```
mvn spring-boot:run
```

## API REST
La aplicación expone los siguientes endpoints:

### Proyectos
- `GET /api/projects`: Obtener todos los proyectos.
- `POST /api/projects`: Crear un nuevo proyecto.
- `GET /api/projects/{id}`: Obtener un proyecto por ID.
- `PUT /api/projects/{id}`: Actualizar un proyecto existente.
- `DELETE /api/projects/{id}`: Eliminar un proyecto.

### Tareas
- `GET /api/projects/{projectId}/tasks`: Obtener todas las tareas de un proyecto.
- `POST /api/projects/{projectId}/tasks`: Crear una nueva tarea en un proyecto.
- `GET /api/projects/{projectId}/tasks/{taskId}`: Obtener una tarea por ID.
- `PUT /api/projects/{projectId}/tasks/{taskId}`: Actualizar una tarea existente.
- `DELETE /api/projects/{projectId}/tasks/{taskId}`: Eliminar una tarea.

## Pruebas
Las pruebas se encuentran en `src/test/java/com/tfg/app/OrkestriaBackendApplicationTests.java`. Puedes ejecutar las pruebas utilizando el siguiente comando:
```
mvn test
```

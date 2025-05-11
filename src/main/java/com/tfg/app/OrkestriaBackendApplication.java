package com.tfg.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrkestriaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrkestriaBackendApplication.class, args);
        System.out.println("Orkestria Backend Application is running on port 8080");
        System.out.println("You can access API documentation at: http://localhost:8080/swagger-ui.html");
    }
}
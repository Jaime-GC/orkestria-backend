package com.tfg.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrkestriaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrkestriaBackendApplication.class, args);
        System.out.println("Orkestria Backend Application is running...");
    }
}
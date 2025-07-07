package com.tfg.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in local environment");

        Contact contact = new Contact();
        contact.setName("Orkestria");
        contact.setEmail("info@orkestria.com");
        contact.setUrl("https://www.orkestria.com");

        License license = new License()
                .name("Apache License, Version 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Orkestria Backend API")
                .version("1.0")
                .contact(contact)
                .description("API for managing projects, tasks, users and resources.")
                .license(license);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}

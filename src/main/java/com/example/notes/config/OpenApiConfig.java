package com.example.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notesApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notes Management API")
                        .description("REST API for creating, updating, retrieving, and deleting notes")
                        .version("v1")
                        .contact(new Contact().name("Notes API Team")));
    }
}

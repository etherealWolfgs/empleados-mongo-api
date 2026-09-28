package com.academia.empleados.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Lo que se ve arriba en Swagger UI: título, versión y descripción de la API
    @Bean
    public OpenAPI empleadosOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Empleados API")
                .version("1.0")
                .description("CRUD de empleados con Spring Boot y MongoDB — Academia Java CDMX"));
    }
}

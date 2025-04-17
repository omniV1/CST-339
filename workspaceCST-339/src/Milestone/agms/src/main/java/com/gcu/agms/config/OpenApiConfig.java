package com.gcu.agms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AGMS API Documentation")
                        .description("API documentation for the AGMS (Application Management System)")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("GCU")
                                .email("support@gcu.edu")
                                .url("https://www.gcu.edu"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
} 
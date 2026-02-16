package com.myauth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info apiInfo = new Info()
            .title("MyAuth API")
            .version("1.0")
            .description("API for creating and managing 2FA tokens")
            .contact(
                new Contact()
                    .name("Rodrigo Rodrigues")
                    .email("rodrigomiguelsr2004@gmail.com")
            );

        return new OpenAPI()
                .info(apiInfo)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                    new Components()
                        .addSecuritySchemes(
                        "bearerAuth", 
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                        )
                );
    }
}
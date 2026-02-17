package com.myauth.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

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
                .addServersItem(new Server().url("http://localhost:8080").description("Local development server"))
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

    @Bean
    public OpenApiCustomizer globalResponses() {
        return openApi -> {
            Schema<?> errorSchema = new Schema<>().$ref("#/components/schemas/ErrorDto");
            MediaType mediaType = new MediaType().schema(errorSchema);
            Content jsonErrorContent = new Content().addMediaType("application/json", mediaType);

            ApiResponse badRequest = new ApiResponse()
                    .description("Malformed Request")
                    .content(jsonErrorContent);

            ApiResponse internalServerError = new ApiResponse()
                    .description("Internal Server Error")
                    .content(jsonErrorContent);
            
            ApiResponse unauthorized = new ApiResponse()
                    .description("Invalid or missing authentication token")
                    .content(jsonErrorContent);
            
            ApiResponse forbidden = new ApiResponse()
                    .description("No permission to access this resource")
                    .content(jsonErrorContent);
            
            ApiResponse methodNotAllowed = new ApiResponse()
                    .description("HTTP method not supported for this endpoint")
                    .content(jsonErrorContent);

            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    ApiResponses responses = operation.getResponses();

                    if (!responses.containsKey("400")) responses.addApiResponse("400", badRequest);
                    if (!responses.containsKey("500")) responses.addApiResponse("500", internalServerError);
                    if (!responses.containsKey("401")) responses.addApiResponse("401", unauthorized);
                    if (!responses.containsKey("403")) responses.addApiResponse("403", forbidden);
                    if (!responses.containsKey("405")) responses.addApiResponse("405", methodNotAllowed);
                });
            });
        };
    }
}
package com.myauth.conf.spring;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class PostRunPrint {
    private final String port = "8080";

    @EventListener(ApplicationReadyEvent.class)
    public void printSwaggerLinks() {
        System.out.println("===================================================================");
        System.out.println("Swagger UI: http://localhost:" + port + "/swagger-ui/index.html");
        System.out.println("API Docs:   http://localhost:" + port + "/v3/api-docs");
        System.out.println("===================================================================");
    }
}
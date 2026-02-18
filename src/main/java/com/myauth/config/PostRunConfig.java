package com.myauth.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Configuration
@Profile("dev")
public class PostRunConfig {
    private final WebServerApplicationContext server;
    private final Environment environment;

    public PostRunConfig(WebServerApplicationContext server, Environment environment) {
        this.server = server;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printSwaggerLinks() {
        int port = server.getWebServer().getPort();
        
        String contextPath = environment.getProperty("server.servlet.context-path", "");

        final String RESET = "\033[0m";
        final String BOLD = "\033[1m";
        final String GREEN = "\033[32m";
        final String BLUE = "\033[34m";
        final String CYAN = "\033[36m";
        final String YELLOW = "\033[33m";

        System.out.println(String.format("""
            %s
            ----------------------------------------------------------
            🚀  %sApplication is running!%s
            ----------------------------------------------------------
            
            🔓  %sLocal Access:%s     http://localhost:%d%s
            
            📄  %sSwagger UI:%s       %shttp://localhost:%d%s/swagger-ui/index.html%s
            ⚡  %sScalar UI:%s        %shttp://localhost:%d%s/scalar%s
            
            ----------------------------------------------------------
            %s""",
            GREEN,
            BOLD + YELLOW, RESET + GREEN,
            BOLD, RESET, port, contextPath,
            BOLD + CYAN, RESET, BLUE, port, contextPath, RESET + GREEN,
            BOLD + CYAN, RESET, BLUE, port, contextPath, RESET + GREEN,
            RESET
        ));
    }
}
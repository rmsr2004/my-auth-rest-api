package com.myauth.IntegrationTests.Configuration.Containers;

import java.time.Duration;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerConfig {
    
    private static final PostgreSQLContainer<?> postgres;
    
    static {
        postgres = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withStartupTimeout(Duration.ofSeconds(120))
                .withStartupAttempts(3)
                .withCommand("postgres", "-c", "fsync=off", "-c", "max_connections=100")
                .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        postgres.start();
    }
    
    public static PostgreSQLContainer<?> getPostgreSQLContainer() {
        return postgres;
    }
}
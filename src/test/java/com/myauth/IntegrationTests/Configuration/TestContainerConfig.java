package com.myauth.IntegrationTests.Configuration;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerConfig {
    
    private static final PostgreSQLContainer<?> postgres;
    
    static {
        postgres = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgres.start();
    }
    
    public static PostgreSQLContainer<?> getPostgreSQLContainer() {
        return postgres;
    }
}
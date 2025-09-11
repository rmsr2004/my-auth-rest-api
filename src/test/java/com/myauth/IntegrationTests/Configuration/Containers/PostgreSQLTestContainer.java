package com.myauth.IntegrationTests.Configuration.Containers;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgreSQLTestContainer {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private static final PostgreSQLContainer<?> postgres = TestContainerConfig.getPostgreSQLContainer();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void setup() {
        System.out.println("=== TESTCONTAINERS DEBUG INFO ===");
        System.out.println("Docker available: " + DockerClientFactory.instance().isDockerAvailable());
        System.out.println("Container JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("Container running: " + postgres.isRunning());
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("SET session_replication_role = 'replica'");
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT tablename FROM pg_tables WHERE schemaname='public'",
                String.class
        );
        tables.forEach(t -> jdbcTemplate.execute("TRUNCATE TABLE " + t + " RESTART IDENTITY CASCADE"));
        jdbcTemplate.execute("SET session_replication_role = 'origin'");
    }
}
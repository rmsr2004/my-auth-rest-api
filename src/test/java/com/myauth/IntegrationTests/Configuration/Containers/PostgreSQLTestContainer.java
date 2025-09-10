package com.myauth.IntegrationTests.Configuration.Containers;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.IntegrationTests.Configuration.TestContainerConfig;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public abstract class PostgreSQLTestContainer {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        PostgreSQLContainer<?> postgres = TestContainerConfig.getPostgreSQLContainer();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
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
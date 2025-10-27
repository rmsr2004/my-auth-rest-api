package com.myauth.IntegrationTests.Features;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;
import com.myauth.common.utils.ErrorDto;
import com.myauth.features.userregistration.RegisterRequestDto;
import com.myauth.features.userregistration.RegisterResponseDto;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("User Registration Integration Tests")
class UserRegistrationTests  {
    @LocalServerPort
    private int port;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");

        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
    }

    @Test
    @DisplayName("Should return user details when user is valid")
    void UserRegistration_ShouldReturn201_WhenRequestIsValid() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("username", "password");

        // Act
        HttpResponse<RegisterResponseDto> response = HttpClient.post("/register", request, RegisterResponseDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        RegisterResponseDto result = response.body();

        assertThat(result.id()).isEqualTo("1");
        assertThat(result.username()).isEqualTo("username");
        assertThat(result.message()).isEqualTo("User successfully registered!");
    }

    @Test
    @DisplayName("Should return error message when user already exists")
    void UserRegistration_ShouldReturn409_WhenRequestIsInvalid() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("username");
        existingUser.setPassword("password");
        userRepository.save(existingUser);

        RegisterRequestDto request = new RegisterRequestDto("username", "password");

        // Act
        HttpResponse<ErrorDto> secondResponse = HttpClient.post("/register", request, ErrorDto.class);

        // Assert
        assertThat(secondResponse).isNotNull();
        assertThat(secondResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

        ErrorDto result = secondResponse.body();
        assertThat(result.error()).isEqualTo("Conflict");
        assertThat(result.message()).isEqualTo("User already exists!");
    }
}

package com.myauth.IntegrationTests.Features;

import static com.myauth.IntegrationTests.Configuration.Containers.PostgreSQLTestContainer.postgres;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.Api.Features.UserLogin.LoginRequestDto;
import com.myauth.Api.Features.UserLogin.LoginResponseDto;
import com.myauth.Infrastructure.Repositories.Entities.UserEntity;
import com.myauth.Infrastructure.Repositories.IUserRepository;
import com.myauth.IntegrationTests.Configuration.Containers.PostgreSQLTestContainer;
import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("User Login Integration Tests")
class UserLoginTests {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
    }

    @Test
    @DisplayName("Should return jwt token when user is valid")
    void UserLogin_ShouldReturn200_WhenRequestIsValid() {
        // Pre-Arrange - Create user in database
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);
        
        // Arrange
        LoginRequestDto request = new LoginRequestDto("username", "password");

        // Act
        HttpResponse<LoginResponseDto> response = HttpClient.put("/login", request, LoginResponseDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LoginResponseDto result = response.body();

        assertThat(result.token()).isNotNull();
        assertThat(result.message()).isEqualTo("User successfully logged in!");
    }

    @Test
    @DisplayName("Should return 404 when user does not exist")
    void UserLogin_ShouldReturn404_WhenUserDoesNotExist() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("nonexistentuser", "password");

        // Act
        HttpResponse<LoginResponseDto> response = HttpClient.put("/login", request, LoginResponseDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        
        LoginResponseDto result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.token()).isNull();
        assertThat(result.message()).isEqualTo("User not found!");
    }

    @Test
    @DisplayName("Should return 401 when password is incorrect")
    void UserLogin_ShouldReturn401_WhenPasswordIsIncorrect() {
        // Pre-Arrange - Create user in database
        UserEntity user = new UserEntity();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("correctpassword"));
        userRepository.save(user);

        // Arrange
        LoginRequestDto request = new LoginRequestDto("username", "wrongpassword");

        // Act
        HttpResponse<LoginResponseDto> response = HttpClient.put("/login", request, LoginResponseDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        LoginResponseDto result = response.body();
        
        assertThat(result).isNotNull();
        assertThat(result.token()).isNull();
        assertThat(result.message()).isEqualTo("User not authorized!");
    }
}

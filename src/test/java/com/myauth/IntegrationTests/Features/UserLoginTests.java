package com.myauth.IntegrationTests.Features;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;
import com.myauth.features.User.userlogin.LoginRequest;
import com.myauth.features.User.userlogin.LoginResponse;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;

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
        userRepository.deleteAll();
        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
    }

    @Test
    @DisplayName("Should return jwt token when user is valid")
    void UserLogin_ShouldReturn200_WhenRequestIsValid() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);
        
        LoginRequest request = new LoginRequest("username", "password");

        // Act
        HttpResponse<LoginResponse> response = HttpClient.put("/login", request, LoginResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LoginResponse result = response.body();

        assertThat(result.token()).isNotNull();
        assertThat(result.message()).isEqualTo("User { username } successfully logged in!");
    }

    @Test
    @DisplayName("Should return 404 when user does not exist")
    void UserLogin_ShouldReturn404_WhenUserDoesNotExist() {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistentuser", "password");

        // Act
        HttpResponse<LoginResponse> response = HttpClient.put("/login", request, LoginResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        
        LoginResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.token()).isNull();
        assertThat(result.message()).isEqualTo("User not found!");
    }

    @Test
    @DisplayName("Should return 401 when password is incorrect")
    void UserLogin_ShouldReturn401_WhenPasswordIsIncorrect() {
        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("correctpassword"));
        userRepository.save(user);

        LoginRequest request = new LoginRequest("username", "wrongpassword");

        // Act
        HttpResponse<LoginResponse> response = HttpClient.put("/login", request, LoginResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        LoginResponse result = response.body();
        
        assertThat(result).isNotNull();
        assertThat(result.token()).isNull();
        assertThat(result.message()).isEqualTo("User not authorized!");
    }
}

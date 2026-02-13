package com.myauth.IntegrationTests.Features;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;
import com.myauth.conf.spring.security.TokenService;
import com.myauth.features.Secret.addsecret.AddSecretRequest;
import com.myauth.features.Secret.addsecret.AddSecretResponse;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Add Secret Integration Tests")
class AddSecretTests {
    @Autowired
    private ISecretRepository secretRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    @SuppressWarnings("unused")
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        secretRepository.deleteAll();
        userRepository.deleteAll();

        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
        HttpClient.setAuthToken("");
    }

    @Test
    @DisplayName("Should return success when secret is valid and user is authenticated")
    void AddSecret_ShouldReturn200_WhenRequestIsValid() {
        // Arrange

        // Authenticate user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        userRepository.save(user);

        String token = tokenService.generateToken(user);
        HttpClient.setAuthToken(token);

        String secret = "mySecret";
        String issuer = "myIssuer";

        AddSecretRequest request = new AddSecretRequest(secret, issuer);
        
        // Act
        HttpResponse<AddSecretResponse> response = HttpClient.post("/secrets", request, AddSecretResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(201);

        AddSecretResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.issuer()).isEqualTo(issuer);
        assertThat(result.message()).isEqualTo("Secret successfully created");
    }

    @Test
    @DisplayName("Should return 409 when issuer already exists for the user")
    void AddSecret_ShouldReturn409_WhenIssuerAlreadyExists() {
        // Arrange

        // Authenticate user
        User user = new User();
        user.setUsername("testuser2");
        user.setPassword("testpassword2");
        userRepository.save(user);

        String token = tokenService.generateToken(user);
        HttpClient.setAuthToken(token);

        String secret = "mySecret";
        String issuer = "myIssuer";

        // Pre-insert a secret with the same issuer for the user
        Secret existingSecret = new Secret();
        existingSecret.setIssuer(issuer);
        existingSecret.setSecret(secret);
        existingSecret.setUser(user);
        
        secretRepository.save(existingSecret);

        AddSecretRequest request = new AddSecretRequest(secret, issuer);
        
        // Act
        HttpResponse<AddSecretResponse> response = HttpClient.post("/secrets", request, AddSecretResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

        AddSecretResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Issuer already exists!");
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void AddSecret_ShouldReturn401_WhenUserIsNotAuthenticated() {
        // Arrange
        String secret = "mysecret";
        String issuer = "myissuer";
        
        AddSecretRequest request = new AddSecretRequest(secret, issuer);
        
        // Act
        HttpResponse<AddSecretResponse> response = HttpClient.post("/secrets", request, AddSecretResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        AddSecretResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Authentication is required to access this resource");
    }
}

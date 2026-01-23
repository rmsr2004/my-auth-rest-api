package com.myauth.IntegrationTests.Features;

import java.util.Optional;

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
import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Errors;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Delete Secret Integration Tests")
class DeleteSecretTests {
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
    @DisplayName("Should return 204 when secret exists and user is owner")
    void DeleteSecret_ShouldReturn204_WhenRequestIsValid() {
        // Arrange
        User user = new User();
        user.setUsername("ownerUser");
        user.setPassword("password");
        userRepository.save(user);

        String token = tokenService.generateToken(user);
        HttpClient.setAuthToken(token);

        Secret secret = new Secret();
        secret.setIssuer("Google");
        secret.setSecret("JBSWY3DPEHPK3PXP");
        secret.setUser(user);
        secretRepository.save(secret);

        // Act
        HttpResponse<Void> response = HttpClient.delete("/secrets/" + secret.getId(), Void.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Optional<Secret> deletedSecret = secretRepository.findById(secret.getId());
        assertThat(deletedSecret).isEmpty();
    }

    @Test
    @DisplayName("Should return 404 when secret id does not exist")
    void DeleteSecret_ShouldReturn404_WhenSecretNotFound() {
        // Arrange
        User user = new User();
        user.setUsername("validUser");
        user.setPassword("password");
        userRepository.save(user);

        String token = tokenService.generateToken(user);
        HttpClient.setAuthToken(token);

        Long nonExistentId = 9999L;

        // Act
        HttpResponse<ErrorDto> response = HttpClient.delete("/secrets/" + nonExistentId, ErrorDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        
        ErrorDto body = response.body();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo(Errors.SECRET_NOT_FOUND.message());
    }

    @Test
    @DisplayName("Should return 403 when trying to delete another user's secret")
    void DeleteSecret_ShouldReturn403_WhenUserIsNotOwner() {
        // Arrange - Victim
        User victim = new User();
        victim.setUsername("victim");
        victim.setPassword("pass");
        userRepository.save(victim);

        Secret victimSecret = new Secret();
        victimSecret.setIssuer("Facebook");
        victimSecret.setSecret("VICTIMSECRET");
        victimSecret.setUser(victim);
        secretRepository.save(victimSecret);

        // Arrange - Attacker
        User attacker = new User();
        attacker.setUsername("attacker");
        attacker.setPassword("pass");
        userRepository.save(attacker);

        String attackerToken = tokenService.generateToken(attacker);
        HttpClient.setAuthToken(attackerToken);

        // Act
        HttpResponse<ErrorDto> response = HttpClient.delete("/secrets/" + victimSecret.getId(), ErrorDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

        Optional<Secret> secretInDb = secretRepository.findById(victimSecret.getId());
        assertThat(secretInDb).isPresent();
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void DeleteSecret_ShouldReturn401_WhenUserIsNotAuthenticated() {
        // Arrange
        HttpClient.setAuthToken("");
        Long anyId = 1L;

        // Act
        HttpResponse<ErrorDto> response = HttpClient.delete("/secrets/" + anyId, ErrorDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ErrorDto result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Authentication is required to access this resource");
    }
}

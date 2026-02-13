package com.myauth.IntegrationTests.Features;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;
import com.myauth.conf.spring.security.TokenService;
import com.myauth.features.Secret.getsecrets.GetSecretsResponse;
import com.myauth.features.Secret.getsecrets.GetSecretsResponse.SecretDto;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Get Secrets Integration Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class GetSecretsTests {
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
    @DisplayName("Should return 200 with list of secrets") 
    void GetSecrets_ShouldReturn200WithListOfSecrets_WhenRequestIsValid() {
        // Arrange
        fillDatabase(true, true);
        
        // Act
        HttpResponse<GetSecretsResponse> response = HttpClient.get("/secrets", GetSecretsResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(200);
        
        GetSecretsResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.secrets()).hasSize(2);

        List<SecretDto> secrets = result.secrets();

        assertThat(secrets.get(0).issuer()).isEqualTo("Issuer1");
        assertThat(secrets.get(0).value()).isEqualTo("Value1");

        assertThat(secrets.get(1).issuer()).isEqualTo("Issuer2");
        assertThat(secrets.get(1).value()).isEqualTo("Value2");
    }

    @Test
    @DisplayName("Should return 200 with empty list") 
    void GetSecrets_ShouldReturn200WithEmptyList_WhenRequestIsValid() {
        // Arrange
        fillDatabase(true, false);
        
        // Act
        HttpResponse<GetSecretsResponse> response = HttpClient.get("/secrets", GetSecretsResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(200);
        
        GetSecretsResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.secrets()).isEmpty();
    }
    
    void fillDatabase(boolean withUsers, boolean withSecrets) {
        secretRepository.deleteAll();
        userRepository.deleteAll();

        if (withUsers) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("hashedpassword");
            userRepository.save(user);

            String token = tokenService.generateToken(user);
            HttpClient.setAuthToken(token);

            if (withSecrets) {
                Secret secret1 = new Secret();
                secret1.setIssuer("Issuer1");
                secret1.setSecret("Value1");
                secret1.setUser(user);
                
                Secret secret2 = new Secret();
                secret2.setIssuer("Issuer2");   
                secret2.setSecret("Value2");
                secret2.setUser(user);
                
                secretRepository.save(secret1);
                secretRepository.save(secret2);
            }
        }

    }
}

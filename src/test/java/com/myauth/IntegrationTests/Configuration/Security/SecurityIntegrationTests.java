package com.myauth.IntegrationTests.Configuration.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.TokenService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security Integration Tests - Access Control")
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should allow access to Login endpoint without token")
    void shouldAllowPublicLogin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); 
    }

    @Test
    @DisplayName("Should allow access to Register endpoint without token")
    void shouldAllowPublicRegister() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should allow access to Swagger UI without token")
    void shouldAllowSwagger() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should BLOCK access to protected endpoints without token")
    void shouldBlockProtectedEndpointNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should BLOCK access with INVALID token")
    void shouldBlockProtectedEndpointInvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/devices")
                .header("Authorization", "Bearer token_falso_12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should BLOCK access with Malformed header")
    void shouldBlockMalformedHeader() throws Exception {
        mockMvc.perform(get("/api/v1/devices")
                .header("Authorization", "token_sem_bearer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should ALLOW access with VALID token")
    void shouldAllowAccessWithValidToken() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user = userRepository.save(user);

        String validToken = tokenService.generateToken(user);

        mockMvc.perform(get("/api/v1/devices")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should NOT create session cookies (Stateless)")
    void shouldBeStateless() throws Exception {
        User user = new User();
        user.setUsername("stateless");
        user.setPassword("example");
        userRepository.save(user);
        String token = tokenService.generateToken(user);

        mockMvc.perform(get("/api/v1/devices")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("JSESSIONID"));
    }
}
package com.myauth.DomainTests.Infrastructure.Security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.security.TokenService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("Token Service Tests")
class TokenServiceTests {
    private TokenService tokenService;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        tokenService = new TokenService();

        ReflectionTestUtils.setField(tokenService, "secret", "um-segredo-muito-seguro-com-mais-de-256-bits-para-o-jwt");
        
        ReflectionTestUtils.setField(tokenService, "expirationDate", 3600000L); 
    }

    @Test
    @DisplayName("Should generate a valid token for a user")
    void shouldGenerateToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("example");

        String token = tokenService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should validate a valid token and return username")
    void shouldValidateToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("validUser");
        user.setPassword("example");

        String token = tokenService.generateToken(user);

        Long subject = tokenService.validateToken(token);

        assertThat(subject).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return empty/null or throw exception for invalid token")
    void shouldFailInvalidToken() {
        String invalidToken = "invalid.fake.token";

        Long token = tokenService.validateToken(invalidToken);

        assertThat(token).isNull();
    }
}
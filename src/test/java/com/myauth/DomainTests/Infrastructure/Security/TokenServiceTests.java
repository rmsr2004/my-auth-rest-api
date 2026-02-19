package com.myauth.DomainTests.Infrastructure.Security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.TokenService;

@DisplayName("Token Service Tests")
class TokenServiceTests {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserRepository userRepository;

    @Test
    @DisplayName("Should generate a valid token for a user")
    void shouldGenerateToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("example");
        userRepository.save(user);

        String token = tokenService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should validate a valid token and return username")
    void shouldValidateToken() {
        User user = new User();
        user.setUsername("validUser");
        user.setPassword("example");
        userRepository.save(user);

        String token = tokenService.generateToken(user);

        Long subject = tokenService.validateToken(token);

        assertThat(subject).isEqualTo(userRepository.findByUsername("validUser").get().getId());
    }

    @Test
    @DisplayName("Should return empty/null or throw exception for invalid token")
    void shouldFailInvalidToken() {
        String invalidToken = "invalid.fake.token";

        Long token = tokenService.validateToken(invalidToken);

        assertThat(token).isNull();
    }
}
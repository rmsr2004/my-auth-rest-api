package com.myauth.DomainTests.Infrastructure.Security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Password Encoder Tests")
class PasswordEncoderTests {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Should encode password correctly (Results should be different for same input due to salt)")
    void shouldEncodePassword() {
        String rawPassword = "minhaSenhaSegura123";

        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        assertThat(encoded1).isNotEqualTo(rawPassword);
        assertThat(encoded1).isNotEqualTo(encoded2);
    }

    @Test
    @DisplayName("Should match raw password with encoded password")
    void shouldMatchPassword() {
        String rawPassword = "123";
        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encoded);
        boolean wrongMatches = passwordEncoder.matches("wrongPass", encoded);

        assertThat(matches).isTrue();
        assertThat(wrongMatches).isFalse();
    }
}
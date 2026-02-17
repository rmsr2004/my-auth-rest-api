package com.myauth.IntegrationTests.Configuration.Security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.CustomUserDetailsService;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("User Details Service Tests")
class CustomUserDetailsServiceTests {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should load existing user by username")
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setUsername("dbUser");
        user.setPassword("encodedPass");
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("dbUser");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("dbUser");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPass");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("ghostUser");
        });

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("User not found");
    }
}
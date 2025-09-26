package com.myauth.DomainTests;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.features.userregistration.UserRegistrationHandler;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Registration Unit Tests")
class UserRegistrationTests {
    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationHandler handler;

    @BeforeEach
    public void setup() {
        handler = new UserRegistrationHandler(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should return Success when user is valid")
    public void UserRegistration_ShouldReturnUserDetails_WhenRequestIsValid() {
        // Arrange
        User user = new User(1L, "username", "password", null, null);
        User savedEntity = new User(1L, "username", "encodedPassword", null, null);

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        // Act
        Result<User> result = handler.register(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getUsername()).isEqualTo("username");
        assertThat(result.getValue().getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getValue().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return failure when user already exists")
    public void UserRegistration_ShouldReturnFailure_WhenUserAlreadyExists() {
        // Arrange
        User user = new User(1L, "username", "password", null, null);
        User savedEntity = new User(1L, "username", "encodedPassword", null, null);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(savedEntity));

        // Act
        Result<User> result = handler.register(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo(Errors.USER_ALREADY_EXISTS);
        assertThat(result.getValue()).isNull();
    }
}
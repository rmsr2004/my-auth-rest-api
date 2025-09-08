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

import com.myauth.Api.Services.AuthService;
import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Shared.Errors;
import com.myauth.Domain.Shared.Result;
import com.myauth.Infrastructure.Repositories.Entities.UserEntity;
import com.myauth.Infrastructure.Repositories.IUserRepository;
import com.myauth.Infrastructure.Repositories.Mappers.UserMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Registration Unit Tests")
class UserRegistrationTests {
    @Mock
    private IUserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        userMapper = new UserMapper(passwordEncoder);
        authService = new AuthService(userRepository, null, userMapper, null);
    }

    @Test
    @DisplayName("Should return Success when user is valid")
    public void UserRegistration_ShouldReturnUserDetails_WhenRequestIsValid() {
        // Arrange
        User user = new User("username", "password");
        UserEntity savedEntity = new UserEntity(1L, "username", "encodedPassword", null);

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // Act
        Result<User> result = authService.register(user);

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
        User user = new User("username", "password");
        UserEntity savedEntity = new UserEntity(1L, "username", "encodedPassword", null);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(savedEntity));

        // Act
        Result<User> result = authService.register(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo(Errors.USER_ALREADY_EXISTS);
        assertThat(result.getValue()).isNull();
    }
}
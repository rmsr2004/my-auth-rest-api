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
import com.myauth.conf.spring.security.TokenService;
import com.myauth.features.userlogin.UserLoginHandler;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Login Unit Tests")
class UserLoginTests {
    @Mock
    private IUserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserLoginHandler handler;

    @BeforeEach
    public void setup() {
        handler = new UserLoginHandler(userRepository, tokenService, passwordEncoder);
    }

    @Test
    @DisplayName("Should return success when user is valid")
    public void UserLogin_ShouldReturnJWTtoken_WhenRequestIsValid() {
        // Arrange
        User user = new User(1L, "username", "password", null, null);
        User savedEntity = new User(1L, "username", "encodedPassword", null, null);

        when(passwordEncoder.matches(user.getPassword(), savedEntity.getPassword())).thenReturn(true);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(savedEntity));
        when(tokenService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        Result<String> result = handler.login(user);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo("jwtToken");
    }

    @Test
    @DisplayName("Should return failure when user not found")
    public void UserLogin_ShouldReturnFailure_WhenUserNotFound() {
        // Arrange
        User user = new User(1L, "username", "password", null, null);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act
        Result<String> result = handler.login(user);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo(Errors.USER_NOT_FOUND);
        assertThat(result.getValue()).isNull();
    }

    @Test
    @DisplayName("Should return failure when password is invalid")
    public void UserLogin_ShouldReturnFailure_WhenPasswordIsInvalid() {
        // Arrange
        User user = new User(1L, "username", "password", null, null);
        User savedEntity = new User(1L, "username", "encodedPassword", null, null);
        
        when(passwordEncoder.matches(user.getPassword(), savedEntity.getPassword())).thenReturn(false);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(savedEntity));

        // Act
        Result<String> result = handler.login(user);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo(Errors.USER_UNAUTHORIZED);
        assertThat(result.getValue()).isNull();
    }
}
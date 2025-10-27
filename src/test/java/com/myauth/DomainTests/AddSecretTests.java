package com.myauth.DomainTests;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.features.addsecret.AddSecretHandler;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Add Secret Unit Tests")
class AddSecretTests {
    @Mock
    private ISecretRepository secretRepository;

    @InjectMocks
    private AddSecretHandler handler;

    @BeforeEach
    public void setup() {
        handler = new AddSecretHandler(secretRepository);
    }

    @Test
    @DisplayName("Should return success when secret is added")
    public void AddSecret_ShouldReturnSuccess_WhenRequestIsValid() {
        // Arrange
        User user = new User();
        String secret = "secret";
        String issuer = "issuer";

        when(secretRepository.findByUserAndIssuer(user, issuer)).thenReturn(Optional.empty());
        
        // Act
        Result<Secret> result = handler.addSecret(user, secret, issuer);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getIssuer()).isEqualTo(issuer);
    }

    @Test
    @DisplayName("Should return failure when issuer already exists")
    public void AddSecret_ShouldReturnFailure_WhenIssuerAlreadyExists() {
        // Arrange
        User user = new User();
        String secret = "secret";
        String issuer = "issuer";

        when(secretRepository.findByUserAndIssuer(user, issuer)).thenReturn(Optional.of(new Secret()));

        // Act
        Result<Secret> result = handler.addSecret(user, secret, issuer);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo(Errors.ISSUER_ALREADY_EXISTS);
        assertThat(result.getValue()).isNull();
    }
}
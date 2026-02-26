package com.myauth.DomainTests.Features.Secret;

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

import com.myauth.features.Secret.addsecret.AddSecretHandler;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

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
        Secret secret = new Secret();
        secret.setSecret("secret");
        secret.setIssuer("issuer");

        when(secretRepository.findByUserAndIssuer(user, secret.getIssuer())).thenReturn(Optional.empty());
        
        // Act
        Result<Secret> result = handler.addSecret(user, secret);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getIssuer()).isEqualTo(secret.getIssuer());
    }

    @Test
    @DisplayName("Should return failure when issuer already exists")
    public void AddSecret_ShouldReturnFailure_WhenIssuerAlreadyExists() {
        // Arrange
        User user = new User();
        Secret secret = new Secret();
        secret.setSecret("secret");
        secret.setIssuer("issuer");

        when(secretRepository.findByUserAndIssuer(user, secret.getIssuer())).thenReturn(Optional.of(new Secret()));

        // Act
        Result<Secret> result = handler.addSecret(user, secret);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo(Errors.ISSUER_ALREADY_EXISTS);
        assertThat(result.getValue()).isNull();
    }
}
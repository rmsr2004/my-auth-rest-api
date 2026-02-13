package com.myauth.DomainTests;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myauth.common.utils.Result;
import com.myauth.features.Secret.getsecrets.GetSecretsHandler;
import com.myauth.features.Secret.getsecrets.GetSecretsResponse.SecretDto;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Secrets Unit Tests")
class GetSecretsTests {
    @Mock
    private ISecretRepository secretRepository;

    @InjectMocks
    private GetSecretsHandler handler;

    @BeforeEach
    public void setup() {
        handler = new GetSecretsHandler(secretRepository);
    }

    @Test
    @DisplayName("Should return success when secrets are retrieved")
    public void GetSecrets_ShouldReturnSuccess_WhenRequestIsValid() {
        // Arrange
        User user = new User();
        Secret secret1 = new Secret();
        secret1.setIssuer("issuer1");
        secret1.setSecret("123");
        Secret secret2 = new Secret();
        secret2.setIssuer("issuer2");
        secret2.setSecret("1234");

        List<Secret> secrets = List.of(secret1, secret2);
        
        when(secretRepository.findAllByUserId(user.getId())).thenReturn(secrets);

        // Act
        Result<List<SecretDto>> result = handler.getSecretsForUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().size()).isEqualTo(2);
        assertThat(result.getValue().get(0).issuer()).isEqualTo("issuer1");
        assertThat(result.getValue().get(1).issuer()).isEqualTo("issuer2");
        assertThat(result.getValue().get(0).value()).isEqualTo("123");
        assertThat(result.getValue().get(1).value()).isEqualTo("1234");
    }

    @Test
    @DisplayName("Should return success with empty list when no secrets exist")
    public void GetSecrets_ShouldReturnSuccessWithEmptyList_WhenNoSecretsExist() {
        // Arrange
        User user = new User();
        
        when(secretRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        // Act
        Result<List<SecretDto>> result = handler.getSecretsForUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().isEmpty()).isTrue();
    }
}
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.features.Secret.deletesecret.DeleteSecretHandler;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Secret Unit Tests")
class DeleteSecretTests {
    @Mock
    private ISecretRepository repository;

    @InjectMocks
    private DeleteSecretHandler handler;

    @BeforeEach
    public void setup() {
        handler = new DeleteSecretHandler(repository);
    }

    @Test
    @DisplayName("Should delete secret successfully when user is the owner")
    void DeleteSecret_ShouldDeleteSecret_WhenUserIsOwner() {
        // Arrange
        Long userId = 1L;
        Long secretId = 100L;

        User owner = new User();
        owner.setId(userId);

        Secret secret = new Secret();
        secret.setId(secretId);
        secret.setUser(owner);

        when(repository.findById(secretId)).thenReturn(Optional.of(secret));

        // Act
        Result<Void> result = handler.deleteSecretForUser(secretId, userId);

        // Assert
        assertThat(result.isSuccess()).isTrue();        
        verify(repository, times(1)).delete(secret);
    }

    @Test
    @DisplayName("Should return SECRET_NOT_FOUND when secret id does not exist")
    void DeleteSecret_ShouldReturnError_WhenSecretNotFound() {
        // Arrange
        Long userId = 1L;
        Long secretId = 999L;

        when(repository.findById(secretId)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = handler.deleteSecretForUser(secretId, userId);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.SECRET_NOT_FOUND);
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return USER_UNAUTHORIZED when trying to delete someone else's secret")
    void DeleteSecret_ShouldReturnError_WhenUserIsNotOwner() {
        // Arrange
        Long attackerId = 666L;
        Long victimId = 1L;
        Long secretId = 100L;

        User victim = new User();
        victim.setId(victimId);

        Secret victimSecret = new Secret();
        victimSecret.setId(secretId);
        victimSecret.setUser(victim);

        when(repository.findById(secretId)).thenReturn(Optional.of(victimSecret));

        // Act
        Result<Void> result = handler.deleteSecretForUser(secretId, attackerId);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.USER_FORBIDDEN);
        verify(repository, never()).delete(any());
    }
}
package com.myauth.features.deletesecret;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.repositories.ISecretRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DeleteSecretHandler {
    private final ISecretRepository repository;

    @Transactional
    public Result<Void> deleteSecretForUser(Long secretId, Long userId) {
        Optional<Secret> secretOptional = repository.findById(secretId);

        if (secretOptional.isEmpty()) {
            return Result.failure(Errors.SECRET_NOT_FOUND); 
        }

        Secret secret = secretOptional.get();

        if (!secret.getUser().getId().equals(userId)) {
            return Result.failure(Errors.USER_FORBIDDEN);
        }

        repository.delete(secret);
        return Result.success(null);
    }
}

package com.myauth.features.Secret.addsecret;

import org.springframework.stereotype.Service;

import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddSecretHandler {
    private final ISecretRepository repository;

    public Result<Secret> addSecret(User user, Secret secret) {
        if (repository.findByUserAndIssuer(user, secret.getIssuer()).isPresent()) {
            return Result.failure(Errors.ISSUER_ALREADY_EXISTS);
        }

        secret.setUser(user);
        repository.save(secret);

        return Result.success(secret);
    }
}

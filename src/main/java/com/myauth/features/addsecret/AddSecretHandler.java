package com.myauth.features.addsecret;

import org.springframework.stereotype.Service;

import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddSecretHandler {
    private final ISecretRepository repository;

    public Result<Secret> addSecret(User user, String secret, String issuer) {
        Secret secretEntity = new Secret();
        secretEntity.setIssuer(issuer);
        secretEntity.setSecret(secret);
        secretEntity.setUser(user);
        
        repository.save(secretEntity);

        return Result.success(secretEntity);
    }
}

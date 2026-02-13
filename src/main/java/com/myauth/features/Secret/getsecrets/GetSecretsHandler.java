package com.myauth.features.Secret.getsecrets;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myauth.common.utils.Result;
import com.myauth.features.Secret.getsecrets.GetSecretsResponse.SecretDto;
import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.ISecretRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GetSecretsHandler {
    private final ISecretRepository repository;

    public Result<List<SecretDto>> getSecretsForUser(User user) {
        List<Secret> secrets = repository.findAllByUserId(user.getId());

        if (secrets.isEmpty()) {
            return Result.success(List.of());
        }

        List<SecretDto> secretDtos = new ArrayList<>(secrets.size());

        for (Secret secret : secrets) {
            secretDtos.add(SecretDto.fromEntity(secret));
        }
         
        return Result.success(secretDtos);
    }
}

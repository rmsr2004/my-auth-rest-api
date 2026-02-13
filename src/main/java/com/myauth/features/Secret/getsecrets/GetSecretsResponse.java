package com.myauth.features.Secret.getsecrets;

import java.util.List;

import com.myauth.infrastructure.db.entities.Secret;

public record GetSecretsResponse(List<SecretDto> secrets, String message) {
    public record SecretDto(Long id, String issuer, String value) {
        public static SecretDto fromEntity(Secret secret) {
            return new SecretDto(secret.getId(), secret.getIssuer(), secret.getSecret());
        }
    }
};

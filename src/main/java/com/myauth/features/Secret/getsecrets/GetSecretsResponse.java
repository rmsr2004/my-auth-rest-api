package com.myauth.features.Secret.getsecrets;

import java.util.List;

import com.myauth.infrastructure.db.entities.Secret;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetSecretsResponse(
    @Schema(description="List of secrets belonging to the user", example="[{'id': 1, 'issuer': 'Google', 'value': 'JBSWY3D}")
    List<SecretDto> secrets,
    @Schema(description="Informational message about the operation", example="Secrets successfully retrieved")
    String message
) {
    public record SecretDto(
        @Schema(description="Unique identifier for the secret", example="1")
        Long id, 
        @Schema(description="Issuer of the secret", example="Google")
        String issuer, 
        @Schema(description="Value of the secret", example="JBSWY3D")
        String value
    ) {
        public static SecretDto fromEntity(Secret secret) {
            return new SecretDto(secret.getId(), secret.getIssuer(), secret.getSecret());
        }
    }
};

package com.myauth.features.Secret.addsecret;

import com.myauth.infrastructure.db.entities.Secret;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddSecretRequest(
    @NotBlank(message="Secret cannot be blank")
    @Schema(description="The secret key to be added", example="JBSWY3DPEHPK3PXP")
    String secret,
    @NotBlank(message="Issuer cannot be blank")
    @Schema(description="The issuer of the secret", example="MyApp")
    String issuer
) {
    public Secret toEntity() {
        Secret secret = new Secret();
        secret.setSecret(this.secret);
        secret.setIssuer(this.issuer);
        return secret;
    }
}
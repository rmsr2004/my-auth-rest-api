package com.myauth.features.Secret.addsecret;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddSecretRequest(
    @NotBlank(message="Secret cannot be blank")
    @Schema(description="The secret key to be added", example="JBSWY3DPEHPK3PXP")
    String secret,
    @NotBlank(message="Issuer cannot be blank")
    @Schema(description="The issuer of the secret", example="MyApp")
    String issuer
) {}
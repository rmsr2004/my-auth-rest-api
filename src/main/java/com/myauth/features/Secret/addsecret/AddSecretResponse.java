package com.myauth.features.Secret.addsecret;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddSecretResponse(
    @Schema(description="The ID of the newly created secret", example="123456789")
    Long id,
    @Schema(description="The issuer of the secret", example="MyApp")
    String issuer,
    @Schema(description="A message confirming successful secret creation", example="Secret successfully created!")
    String message
) {}

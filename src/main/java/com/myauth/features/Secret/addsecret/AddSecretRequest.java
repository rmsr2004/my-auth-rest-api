package com.myauth.features.Secret.addsecret;

import jakarta.validation.constraints.NotBlank;

public record AddSecretRequest(
    @NotBlank(message="")
    String secret,
    @NotBlank(message="")
    String issuer
) {}
package com.myauth.features.addsecret;

import jakarta.validation.constraints.NotBlank;

public record AddSecretRequestDto(
    @NotBlank(message="")
    String secret,
    @NotBlank(message="")
    String issuer
) {}
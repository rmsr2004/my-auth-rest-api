package com.myauth.api.dto.secret;

import jakarta.validation.constraints.NotBlank;

public record SecretRequestDto(
        @NotBlank(message="Issuer must be provided")
        String issuer,
        @NotBlank(message="Secret must be provided")
        String secret
) {
}

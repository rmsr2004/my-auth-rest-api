package com.myauth.api.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message="User name is required")
        String username,
        @NotBlank(message="Password is required")
        String password,
        String deviceId
) {}

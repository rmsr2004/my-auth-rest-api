package com.myauth.api.dtos.register;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank(message="User name is required")
        String username,
        @NotBlank(message="Password is required")
        String password
) {}

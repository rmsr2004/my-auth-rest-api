package com.myauth.Api.Features.UserLogin;

import com.myauth.Domain.Entities.User;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message="User name is required")
        String username,
        @NotBlank(message="Password is required")
        String password
) {
    public User toDomain() {
        return new User(this.username(), this.password());
    }
}
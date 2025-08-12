package com.myauth.Api.Features.UserRegistration;

import com.myauth.Domain.Entities.User;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank(message="Username is required")
        String username,
        @NotBlank(message="Password is required")
        String password
) {
        public User toDomain() {
            return new User(this.username(), this.password());
        }
}

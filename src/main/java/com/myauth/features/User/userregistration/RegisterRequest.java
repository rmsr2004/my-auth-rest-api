package com.myauth.features.User.userregistration;

import com.myauth.infrastructure.db.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Username is required") 
        @Schema(description="The username of the user", example="john_doe")
        String username,
        @NotBlank(message = "Password is required") 
        @Schema(description="The password of the user", example="secure_password")
        String password
) {
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        return user;
    }
}

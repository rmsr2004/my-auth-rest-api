package com.myauth.features.User.userlogin;

import com.myauth.infrastructure.db.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message="User name is required") 
    @Schema(description="The user's username", example="john_doe")
    String username,
    @NotBlank(message="Password is required") 
    @Schema(description="The user's password", example="P@ssw0rd!")
    String password
) {
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        return user;
    }
}
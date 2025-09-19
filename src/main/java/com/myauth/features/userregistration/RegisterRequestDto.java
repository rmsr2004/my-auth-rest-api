package com.myauth.features.userregistration;

import com.myauth.infrastructure.db.entities.User;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank(message = "Username is required") 
        String username,
        @NotBlank(message = "Password is required") 
        String password
) {
    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        return user;
    }
}

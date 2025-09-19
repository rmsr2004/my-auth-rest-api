package com.myauth.features.userlogin;

import com.myauth.infrastructure.db.entities.User;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "User name is required") 
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
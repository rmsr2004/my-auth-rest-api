package com.myauth.features.User.userregistration;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterResponse(
    @Schema(description="The unique identifier of the registered user", example="123e4567-e89b-12d3-a456-426614174000")
    String id, 
    @Schema(description="The username of the registered user", example="john_doe")
    String username, 
    @Schema(description="A message confirming successful registration", example="User { john_doe } successfully registered!")
    String message
) {}

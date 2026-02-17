package com.myauth.features.User.userlogin;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
    @Schema(description="The JWT token for the logged-in user", example="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token, 
    @Schema(description="A message confirming successful login", example="User { john_doe } successfully logged in!")
    String message
) {}

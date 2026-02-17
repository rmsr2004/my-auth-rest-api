package com.myauth.common.utils;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorDto(
    @Schema(description="Timestamp of the error", example="2023-01-01T00:00:00Z")
    String timestamp, 
    @Schema(description="HTTP status code", example="404")
    int status, 
    @Schema(description="Error type", example="NOT_FOUND")
    String error, 
    @Schema(description="Error message", example="Device not found")
    String message, 
    @Schema(description="Request path that caused the error", example="/api/auth/devices/device123")
    String path
) {}
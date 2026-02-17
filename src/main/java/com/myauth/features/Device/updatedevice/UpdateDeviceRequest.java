package com.myauth.features.Device.updatedevice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;

public record UpdateDeviceRequest(
    @Schema(description="New name for the device", example="John's iPhone")
    String name,
    @AssertTrue(message="isAdmin must be true")
    @Schema(description="Whether the device should have admin privileges", example="true")
    Boolean isAdmin
) {};
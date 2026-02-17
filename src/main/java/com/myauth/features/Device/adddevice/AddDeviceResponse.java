package com.myauth.features.Device.adddevice;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddDeviceResponse(
    @Schema(description="Unique identifier for the device", example="device-12345")
    String id,
    @Schema(description="User-friendly name for the device", example="John's iPhone")
    String name,
    @Schema(description="Message describing the device addition", example="Device added successfully")
    String message
) {}

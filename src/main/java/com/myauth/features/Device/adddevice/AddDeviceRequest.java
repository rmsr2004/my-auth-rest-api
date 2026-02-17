package com.myauth.features.Device.adddevice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddDeviceRequest(
    @NotBlank(message="Id must not be blank")
    @Schema(description="Unique identifier for the device", example="device-12345")
    String id,
    @NotBlank(message="Name must not be blank")
    @Schema(description="User-friendly name for the device", example="John's iPhone")
    String name
) {}

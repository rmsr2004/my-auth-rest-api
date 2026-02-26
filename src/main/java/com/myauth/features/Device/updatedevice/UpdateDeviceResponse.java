package com.myauth.features.Device.updatedevice;

import com.myauth.infrastructure.db.entities.Device;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateDeviceResponse(
    @Schema(description="Unique identifier for the device", example="device123")
    String deviceId,
    @Schema(description="Name of the device", example="John's iPhone")
    String name,
    @Schema(description="Whether the device has admin privileges", example="true")
    Boolean isAdmin
) {}

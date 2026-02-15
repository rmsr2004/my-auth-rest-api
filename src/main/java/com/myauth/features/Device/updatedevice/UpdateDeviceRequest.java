package com.myauth.features.Device.updatedevice;

import jakarta.validation.constraints.AssertTrue;

public record UpdateDeviceRequest(
    String name,
    @AssertTrue(message="isAdmin must be true")
    Boolean isAdmin
) {};
package com.myauth.features.Device.adddevice;

import jakarta.validation.constraints.NotBlank;

public record AddDeviceRequest(
    @NotBlank(message = "Id must not be blank")
    String id,
    @NotBlank(message = "Name must not be blank")
    String name
) {}

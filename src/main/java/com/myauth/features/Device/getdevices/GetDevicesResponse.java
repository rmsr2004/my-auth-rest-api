package com.myauth.features.Device.getdevices;

import java.util.List;

import com.myauth.infrastructure.db.entities.Device;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetDevicesResponse(
    @Schema(description="List of devices associated with the user", example="[{'id': 'device123', 'name': 'John's iPhone', 'isAdmin': false}]")
    List<DeviceDto> devices, 
    @Schema(description="Informational message about the operation", example="Devices successfully retrieved")
    String message
) {

    public record DeviceDto(
        @Schema(description="Unique identifier for the device", example="device123")
        String id, 
        @Schema(description="Name of the device", example="John's iPhone")
        String name, 
        @Schema(description="Whether the device has admin privileges", example="false")
        Boolean isAdmin
    ) {
        public static DeviceDto fromEntity(Device device) {
            return new DeviceDto(
                device.getId(), 
                device.getName(), 
                device.getIsAdmin()
            );
        }
    }
}

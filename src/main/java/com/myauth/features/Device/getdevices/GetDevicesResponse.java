package com.myauth.features.Device.getdevices;

import java.util.List;

import com.myauth.infrastructure.db.entities.Device;

public record GetDevicesResponse(List<DeviceDto> devices, String message) {

    public record DeviceDto(String id, String name, Boolean isAdmin) {
        public static DeviceDto fromEntity(Device device) {
            return new DeviceDto(
                device.getId(), 
                device.getName(), 
                device.getIsAdmin()
            );
        }
    }
}

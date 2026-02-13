package com.myauth.features.getdevices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myauth.common.utils.Result;
import com.myauth.features.getdevices.GetDevicesResponse.DeviceDto;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GetDevicesHandler {
    private final IDeviceRepository repository;

    public Result<List<DeviceDto>> getDevicesForUser(User user) {
        List<Device> devices = repository.findAllByUserId(user.getId());

        if (devices.isEmpty()) {
            return Result.success(List.of());
        }

        List<DeviceDto> deviceDtos = new ArrayList<>(devices.size());

        for (Device device : devices) {
            deviceDtos.add(DeviceDto.fromEntity(device));
        }
         
        return Result.success(deviceDtos);
    }
}

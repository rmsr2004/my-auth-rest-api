package com.myauth.features.Device.updatedevice;

import org.springframework.stereotype.Service;

import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UpdateDeviceHandler {
    private final IDeviceRepository repository;

    public Result<Device> updateDeviceForUser(
        String deviceIdToUpdate, 
        User user, 
        String currentDeviceId, 
        Device device
    ) {
        Device currentDevice = repository.findByUserAndId(user, currentDeviceId).orElse(null);
        if (currentDevice == null) {
            return Result.failure(Errors.DEVICE_NOT_FOUND);
        }   

        Device deviceToUpdate = repository.findByUserAndId(user, deviceIdToUpdate).orElse(null);
        if (deviceToUpdate == null) {
            return Result.failure(Errors.DEVICE_NOT_FOUND);
        }

        if (currentDevice.getIsAdmin() == false) {
            return Result.failure(Errors.DEVICE_FORBIDDEN);
        }

        if (device.getName() != null && !device.getName().trim().isEmpty()) {
            deviceToUpdate.setName(device.getName());
        }

        if (device.getIsAdmin() != null) {
            deviceToUpdate.setIsAdmin(device.getIsAdmin());
            currentDevice.setIsAdmin(false);
            repository.save(currentDevice);
        }

        repository.save(deviceToUpdate);

        return Result.success(deviceToUpdate);
    }
}

package com.myauth.features.Device.deletedevice;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DeleteDeviceHandler {
    private final IDeviceRepository deviceRepository;

    public Result<Void> deleteDeviceForUser(String deviceIdToDelete, Long userId, String currentDeviceId) {
        Optional<Device> deviceToDelete = deviceRepository.findById(deviceIdToDelete);

        if (deviceToDelete.isEmpty()) {
            return Result.failure(Errors.DEVICE_NOT_FOUND);
        }

        Optional<Device> deviceOfCurrentUser = deviceRepository.findById(currentDeviceId);

        if (deviceOfCurrentUser.isEmpty()) {
            return Result.failure(Errors.DEVICE_NOT_FOUND);
        }

        if (deviceIdToDelete.equals(currentDeviceId)) {
            return Result.failure(Errors.DEVICE_FORBIDDEN);
        }

        Boolean isDeviceAdmin = deviceOfCurrentUser.get().getIsAdmin();

        if (!isDeviceAdmin) {
            return Result.failure(Errors.DEVICE_FORBIDDEN);
        }

        deviceRepository.delete(deviceToDelete.get());

        return Result.success(null);
    }
}

package com.myauth.features.Device.adddevice;

import org.springframework.stereotype.Service;

import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddDeviceHandler {
    private final IDeviceRepository repository;

    public Result<Device> addDevice(User user, Device device) {
        if (repository.existsByUserAndId(user, device.getId())) {
            return Result.failure(Errors.DEVICE_ALREADY_EXISTS);
        }

        device.setUser(user);
        device.setIsAdmin(false);

        if (repository.findAllByUserId(user.getId()).isEmpty()) {
            device.setIsAdmin(true);
        }

        repository.save(device);

        return Result.success(device);
    }
}

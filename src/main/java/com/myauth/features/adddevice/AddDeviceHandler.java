package com.myauth.features.adddevice;

import org.springframework.stereotype.Service;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddDeviceHandler {
    private final IDeviceRepository repository;

    public Result<Device> addDevice(User user, String id, String name) {
        if (repository.existsByUserAndId(user, id)) {
            return Result.failure(Errors.DEVICE_ALREADY_EXISTS);
        }

        Device device = new Device();
        device.setUser(user);
        device.setId(id);
        device.setName(name);
        device.setIsAdmin(false);

        if (repository.findAllByUserId(user.getId()).isEmpty()) {
            device.setIsAdmin(true);
        }

        repository.save(device);

        return Result.success(device);
    }
}

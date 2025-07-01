package com.myauth.api.repositories;

import com.myauth.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.api.entities.Device;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByUser(User user);
    Optional<Device> findById(String id);
}

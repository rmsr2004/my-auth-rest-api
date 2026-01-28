package com.myauth.infrastructure.db.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;

public interface IDeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByUser(User user); 
    Boolean existsByUserAndDeviceId(User user, String deviceId);
}
package com.myauth.api.repository;

import com.myauth.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.api.model.Device;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByUser(User user);
}

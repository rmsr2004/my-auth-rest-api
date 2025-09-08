package com.myauth.Infrastructure.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.Infrastructure.Repositories.Entities.DeviceEntity;
import com.myauth.Infrastructure.Repositories.Entities.UserEntity;

public interface IDeviceRepository extends JpaRepository<DeviceEntity, String> {
    Optional<DeviceEntity> findByUser(UserEntity user); 
}
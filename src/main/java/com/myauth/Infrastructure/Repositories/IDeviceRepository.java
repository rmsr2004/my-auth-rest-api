package com.myauth.Infrastructure.Repositories;

import com.myauth.Infrastructure.Repositories.Entities.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDeviceRepository extends JpaRepository<DeviceEntity, String> {}
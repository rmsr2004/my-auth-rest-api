package com.myauth.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.api.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {}

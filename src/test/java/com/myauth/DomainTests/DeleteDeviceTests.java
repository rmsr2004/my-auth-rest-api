package com.myauth.DomainTests;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.features.deletedevice.DeleteDeviceHandler;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Device Unit Tests")
class DeleteDeviceTests {

    @Mock
    private IDeviceRepository deviceRepository;

    @InjectMocks
    private DeleteDeviceHandler handler;

    private final Long USER_ID = 1L;
    private final String TARGET_DEVICE_ID = "target-device-id";
    private final String ADMIN_DEVICE_ID = "admin-device-id";
    private final String NORMAL_DEVICE_ID = "normal-device-id";

    @BeforeEach
    public void setup() {
        handler = new DeleteDeviceHandler(deviceRepository);
    }

    @Test
    @DisplayName("Should delete device successfully when requester is Admin and target exists")
    void DeleteDevice_ShouldReturnSuccess_WhenRequesterIsAdmin() {
        // Arrange
        Device targetDevice = new Device();
        targetDevice.setId(TARGET_DEVICE_ID);

        Device adminDevice = new Device();
        adminDevice.setId(ADMIN_DEVICE_ID);
        adminDevice.setIsAdmin(true);

        when(deviceRepository.findById(TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));
        when(deviceRepository.findById(ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));

        // Act
        Result<Void> result = handler.deleteDeviceForUser(TARGET_DEVICE_ID, USER_ID, ADMIN_DEVICE_ID);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        verify(deviceRepository).delete(targetDevice);
    }

    @Test
    @DisplayName("Should return DEVICE_NOT_FOUND when the device to delete does not exist")
    void DeleteDevice_ShouldReturnError_WhenTargetDeviceNotFound() {
        // Arrange
        when(deviceRepository.findById(TARGET_DEVICE_ID)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = handler.deleteDeviceForUser(TARGET_DEVICE_ID, USER_ID, ADMIN_DEVICE_ID);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_NOT_FOUND);
        verify(deviceRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return DEVICE_NOT_FOUND when the requesting device does not exist")
    void DeleteDevice_ShouldReturnError_WhenCurrentDeviceNotFound() {
        // Arrange
        Device targetDevice = new Device();
        targetDevice.setId(TARGET_DEVICE_ID);

        when(deviceRepository.findById(TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));
        when(deviceRepository.findById(ADMIN_DEVICE_ID)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = handler.deleteDeviceForUser(TARGET_DEVICE_ID, USER_ID, ADMIN_DEVICE_ID);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_NOT_FOUND);
        verify(deviceRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return DEVICE_FORBIDDEN when trying to delete itself")
    void DeleteDevice_ShouldReturnError_WhenDeletingSelf() {
        // Arrange
        Device adminDevice = new Device();
        adminDevice.setId(ADMIN_DEVICE_ID);
        adminDevice.setIsAdmin(true);

        when(deviceRepository.findById(ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));

        // Act
        Result<Void> result = handler.deleteDeviceForUser(ADMIN_DEVICE_ID, USER_ID, ADMIN_DEVICE_ID);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_FORBIDDEN);
        verify(deviceRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return DEVICE_FORBIDDEN when requesting device is NOT an admin")
    void DeleteDevice_ShouldReturnError_WhenRequesterIsNotAdmin() {
        // Arrange
        Device targetDevice = new Device();
        targetDevice.setId(TARGET_DEVICE_ID);

        Device normalDevice = new Device();
        normalDevice.setId(NORMAL_DEVICE_ID);
        normalDevice.setIsAdmin(false);

        when(deviceRepository.findById(TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));
        when(deviceRepository.findById(NORMAL_DEVICE_ID)).thenReturn(Optional.of(normalDevice));

        // Act
        Result<Void> result = handler.deleteDeviceForUser(TARGET_DEVICE_ID, USER_ID, NORMAL_DEVICE_ID);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_FORBIDDEN);
        verify(deviceRepository, never()).delete(any());
    }
}
package com.myauth.DomainTests.Features.Device;

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

import com.myauth.features.Device.updatedevice.UpdateDeviceHandler;
import com.myauth.features.Device.updatedevice.UpdateDeviceRequest;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Device Unit Tests")
class UpdateDeviceTests {
    @Mock
    private IDeviceRepository deviceRepository;

    @InjectMocks
    private UpdateDeviceHandler handler;

    private User user;
    private Device adminDevice;
    private Device targetDevice;
    private final String ADMIN_DEVICE_ID = "admin-dev-id";
    private final String TARGET_DEVICE_ID = "target-dev-id";

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        adminDevice = new Device();
        adminDevice.setId(ADMIN_DEVICE_ID);
        adminDevice.setUser(user);
        adminDevice.setIsAdmin(true);

        targetDevice = new Device();
        targetDevice.setId(TARGET_DEVICE_ID);
        targetDevice.setUser(user);
        targetDevice.setName("Old Name");
        targetDevice.setIsAdmin(false);
    }

    @Test
    @DisplayName("Should update device name successfully when requester is admin")
    void shouldUpdateName_WhenRequesterIsAdmin() {
        // Arrange
        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));
        when(deviceRepository.findByUserAndId(user, TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getName()).isEqualTo("New Name");
        
        // Verifica se salvou
        verify(deviceRepository).save(targetDevice);
        verify(deviceRepository, never()).save(adminDevice);
    }

    @Test
    @DisplayName("Should transfer admin status successfully")
    void shouldUpdateAdminStatus_WhenRequested() {
        // Arrange
        UpdateDeviceRequest request = new UpdateDeviceRequest(null, true);

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));
        when(deviceRepository.findByUserAndId(user, TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(targetDevice.getIsAdmin()).isTrue();
        assertThat(adminDevice.getIsAdmin()).isFalse();

        verify(deviceRepository).save(adminDevice);
        verify(deviceRepository).save(targetDevice);
    }

    @Test
    @DisplayName("Should update both name and admin status")
    void shouldUpdateNameAndAdmin_WhenBothProvided() {
        // Arrange
        UpdateDeviceRequest request = new UpdateDeviceRequest("Super Phone", true);

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));
        when(deviceRepository.findByUserAndId(user, TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getName()).isEqualTo("Super Phone");
        assertThat(result.getValue().getIsAdmin()).isTrue();
        assertThat(adminDevice.getIsAdmin()).isFalse();

        verify(deviceRepository).save(targetDevice);
        verify(deviceRepository).save(adminDevice);
    }

    @Test
    @DisplayName("Should not update name if provided name is empty or blank")
    void shouldNotUpdateName_WhenNameIsBlank() {
        // Arrange
        UpdateDeviceRequest request = new UpdateDeviceRequest("   ", null); // Nome em branco

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));
        when(deviceRepository.findByUserAndId(user, TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getName()).isEqualTo("Old Name"); // Mantém o antigo
        verify(deviceRepository).save(targetDevice);
    }

    @Test
    @DisplayName("Should return DEVICE_NOT_FOUND when current device (header) does not exist")
    void shouldReturnError_WhenCurrentDeviceNotFound() {
        // Arrange
        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.empty());

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_NOT_FOUND);
        verify(deviceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return DEVICE_NOT_FOUND when target device does not exist")
    void shouldReturnError_WhenTargetDeviceNotFound() {
        // Arrange
        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));
        when(deviceRepository.findByUserAndId(user, TARGET_DEVICE_ID)).thenReturn(Optional.empty());

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_NOT_FOUND);
        verify(deviceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return DEVICE_FORBIDDEN when current device is not Admin")
    void shouldReturnError_WhenRequesterIsNotAdmin() {
        // Arrange
        adminDevice.setIsAdmin(false); // O requester não é admin
        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        when(deviceRepository.findByUserAndId(user, ADMIN_DEVICE_ID)).thenReturn(Optional.of(adminDevice));
        when(deviceRepository.findByUserAndId(user, TARGET_DEVICE_ID)).thenReturn(Optional.of(targetDevice));

        // Act
        Result<Device> result = handler.updateDeviceForUser(TARGET_DEVICE_ID, user, ADMIN_DEVICE_ID, request);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_FORBIDDEN);
        verify(deviceRepository, never()).save(any());
    }
}
package com.myauth.DomainTests;

import java.util.List;

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
import com.myauth.features.Device.adddevice.AddDeviceHandler;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Add Device Unit Tests")
public class AddDeviceTests {
    @Mock
    private IDeviceRepository repository;

    @InjectMocks
    private AddDeviceHandler handler;

    @BeforeEach
    public void setup() {
        handler = new AddDeviceHandler(repository);
    }

    @Test
    @DisplayName("Should create Admin Device when database is empty")
    void AddDevice_ShouldCreateAdminDevice_WhenDatabaseIsEmpty() {
        // Arrange
        User user = new User();
        user.setId(1L);
        String deviceId = "unique-id-1";
        String name = "Pixel 8";

        when(repository.existsByUserAndId(user, deviceId)).thenReturn(false);
        when(repository.findAllByUserId(user.getId())).thenReturn(List.of());

        // Act
        Result<Device> result = handler.addDevice(user, deviceId, name);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(deviceId);
        assertThat(result.getValue().getName()).isEqualTo(name);
        assertThat(result.getValue().getIsAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should create Normal Device when database is NOT empty")
    void shouldCreateNormalDevice_WhenDatabaseIsNotEmpty() {
        // Arrange
        User user = new User();
        String deviceId = "unique-id-2";
        String name = "iPhone";

        when(repository.existsByUserAndId(user, deviceId)).thenReturn(false);
        when(repository.findAllByUserId(user.getId())).thenReturn(List.of(new Device()));

        // Act
        Result<Device> result = handler.addDevice(user, deviceId, name);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(deviceId);
        assertThat(result.getValue().getName()).isEqualTo(name);
        assertThat(result.getValue().getIsAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should fail when device ID already exists for user")
    void shouldReturnError_WhenDeviceAlreadyExists() {
        // Arrange
        User user = new User();
        String deviceId = "existing-id";
        String name = "Name";

        when(repository.existsByUserAndId(user, deviceId)).thenReturn(true);

        // Act
        Result<Device> result = handler.addDevice(user, deviceId, name);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_ALREADY_EXISTS);

        verify(repository, never()).save(any());
    }
}

package com.myauth.DomainTests.Features.Device;

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

import com.myauth.features.Device.adddevice.AddDeviceHandler;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

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
        Device device = new Device();
        device.setId("unique-id-1");
        device.setName("Pixel 8");

        when(repository.existsByUserAndId(user, device.getId())).thenReturn(false);
        when(repository.findAllByUserId(user.getId())).thenReturn(List.of());

        // Act
        Result<Device> result = handler.addDevice(user, device);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(device.getId());
        assertThat(result.getValue().getName()).isEqualTo(device.getName());
        assertThat(result.getValue().getIsAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should create Normal Device when database is NOT empty")
    void shouldCreateNormalDevice_WhenDatabaseIsNotEmpty() {
        // Arrange
        User user = new User();
        Device device = new Device();
        device.setId("unique-id-2");
        device.setName("iPhone");

        when(repository.existsByUserAndId(user, device.getId())).thenReturn(false);
        when(repository.findAllByUserId(user.getId())).thenReturn(List.of(new Device()));

        // Act
        Result<Device> result = handler.addDevice(user, device);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(device.getId());
        assertThat(result.getValue().getName()).isEqualTo(device.getName());
        assertThat(result.getValue().getIsAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should fail when device ID already exists for user")
    void shouldReturnError_WhenDeviceAlreadyExists() {
        // Arrange
        User user = new User();
        Device device = new Device();
        device.setId("existing-id");
        device.setName("Name");

        when(repository.existsByUserAndId(user, device.getId())).thenReturn(true);

        // Act
        Result<Device> result = handler.addDevice(user, device);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Errors.DEVICE_ALREADY_EXISTS);

        verify(repository, never()).save(any());
    }
}

package com.myauth.DomainTests;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myauth.common.utils.Result;
import com.myauth.features.Device.getdevices.GetDevicesHandler;
import com.myauth.features.Device.getdevices.GetDevicesResponse.DeviceDto;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Devices Unit Tests")
class GetDevicesTests {
    @Mock
    private IDeviceRepository deviceRepository;

    @InjectMocks
    private GetDevicesHandler handler;

    @BeforeEach
    public void setup() {
        handler = new GetDevicesHandler(deviceRepository);
    }

    @Test
    @DisplayName("Should return success with list of devices when devices exist")
    public void GetDevices_ShouldReturnList_WhenDevicesExist() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Device device1 = new Device();
        device1.setId("device-1");
        device1.setName("iPhone 13");
        device1.setIsAdmin(true);
        device1.setUser(user);

        Device device2 = new Device();
        device2.setId("device-2");
        device2.setName("Samsung S24");
        device2.setIsAdmin(false);
        device2.setUser(user);

        List<Device> devices = List.of(device1, device2);

        when(deviceRepository.findAllByUserId(user.getId())).thenReturn(devices);

        // Act
        Result<List<DeviceDto>> result = handler.getDevicesForUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        
        List<DeviceDto> dtos = result.getValue();
        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);

        assertThat(dtos.get(0).id()).isEqualTo("device-1");
        assertThat(dtos.get(0).name()).isEqualTo("iPhone 13");
        assertThat(dtos.get(0).isAdmin()).isTrue();

        assertThat(dtos.get(1).id()).isEqualTo("device-2");
        assertThat(dtos.get(1).name()).isEqualTo("Samsung S24");
        assertThat(dtos.get(1).isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should return success with empty list when no devices exist")
    public void GetDevices_ShouldReturnEmptyList_WhenNoDevicesExist() {
        // Arrange
        User user = new User();
        user.setId(2L);

        when(deviceRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        // Act
        Result<List<DeviceDto>> result = handler.getDevicesForUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue()).isEmpty();
    }
}
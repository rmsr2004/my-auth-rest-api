package com.myauth.IntegrationTests.Features.Device;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;
import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Errors;
import com.myauth.conf.spring.security.TokenService;
import com.myauth.features.Device.updatedevice.UpdateDeviceRequest;
import com.myauth.features.Device.updatedevice.UpdateDeviceResponse;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Update Device Integration Tests")
class UpdateDeviceTests {

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    @SuppressWarnings("unused")
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        deviceRepository.deleteAll();
        userRepository.deleteAll();

        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
    }

    @Test
    @DisplayName("Should update device name successfully when requester is Admin")
    void updateDevice_ShouldUpdateName_WhenRequesterIsAdmin() {
        // Arrange
        User user = createUser("adminUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "admin-device-id", "Admin Phone", true);        
        createDevice(user, "target-device-id", "Old Name", false);

        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        // Act
        HttpResponse<UpdateDeviceResponse> response = HttpClient.put("/devices/target-device-id", request, "admin-device-id", UpdateDeviceResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        UpdateDeviceResponse body = response.body();
        assertThat(body.name()).isEqualTo("New Name");
        
        Device updatedDevice = deviceRepository.findById("target-device-id").orElseThrow();
        assertThat(updatedDevice.getName()).isEqualTo("New Name");
        assertThat(updatedDevice.getIsAdmin()).isFalse(); // Admin status não mudou
    }

    @Test
    @DisplayName("Should transfer admin status successfully")
    void updateDevice_ShouldTransferAdmin_WhenRequested() {
        // Arrange
        User user = createUser("transferUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "current-admin-id", "Current Admin", true);
        createDevice(user, "future-admin-id", "Future Admin", false);

        UpdateDeviceRequest request = new UpdateDeviceRequest(null, true);

        // Act
        HttpResponse<UpdateDeviceResponse> response = HttpClient.put("/devices/future-admin-id", request, "current-admin-id", UpdateDeviceResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().isAdmin()).isTrue();

        Device target = deviceRepository.findById("future-admin-id").orElseThrow();
        assertThat(target.getIsAdmin()).isTrue();

        Device requester = deviceRepository.findById("current-admin-id").orElseThrow();
        assertThat(requester.getIsAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should return 403 Forbidden when requester is not Admin")
    void updateDevice_ShouldReturn403_WhenRequesterIsNotAdmin() {
        // Arrange
        User user = createUser("normalUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "normal-device-id", "Normal Phone", false);
        createDevice(user, "target-device-id", "Target", false);

        UpdateDeviceRequest request = new UpdateDeviceRequest("Hacker Name", true);

        // Act
        HttpResponse<ErrorDto> response = HttpClient.put("/devices/target-device-id", request, "normal-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_FORBIDDEN.message());
        
        Device target = deviceRepository.findById("target-device-id").orElseThrow();
        assertThat(target.getName()).isEqualTo("Target");
    }

    @Test
    @DisplayName("Should return 404 Not Found when target device does not exist")
    void updateDevice_ShouldReturn404_WhenTargetNotFound() {
        // Arrange
        User user = createUser("searchUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "admin-device-id", "Admin Phone", true);

        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        // Act
        HttpResponse<ErrorDto> response = HttpClient.put("/devices/non-existent-id", request, "admin-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_NOT_FOUND.message());
    }

    @Test
    @DisplayName("Should return 404 Not Found when requesting device (header) does not exist")
    void updateDevice_ShouldReturn404_WhenCurrentDeviceNotFound() {
        // Arrange
        User user = createUser("ghostUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "target-device-id", "Target", false);

        UpdateDeviceRequest request = new UpdateDeviceRequest("New Name", null);

        // Act
        HttpResponse<ErrorDto> response = HttpClient.put("/devices/target-device-id", request, "ghost-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_NOT_FOUND.message());
    }

    // --- Helpers ---

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pass");
        return userRepository.save(user);
    }

    private void createDevice(User user, String id, String name, boolean isAdmin) {
        Device device = new Device();
        device.setId(id);
        device.setName(name);
        device.setIsAdmin(isAdmin);
        device.setUser(user);
        deviceRepository.save(device);
    }
}
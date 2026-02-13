package com.myauth.IntegrationTests.Features;

import java.util.Optional;

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
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Delete Device Integration Tests")
class DeleteDeviceTests {

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
        
        // Configuração padrão para casos que não usam o helper customizado
        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
        HttpClient.setAuthToken("");
    }

    @Test
    @DisplayName("Should return 204 No Content when admin deletes another device successfully")
    void DeleteDevice_ShouldReturn204_WhenAdminDeletesTarget() {
        // Arrange
        User user = createUser("adminUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "admin-device-id", "Admin Phone", true);
        createDevice(user, "target-device-id", "Old Phone", false);

        // Act
        HttpResponse<Void> response = HttpClient.delete("/devices/target-device-id", "admin-device-id", Void.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Optional<Device> deletedDevice = deviceRepository.findById("target-device-id");
        assertThat(deletedDevice).isEmpty();
        
        Optional<Device> adminDevice = deviceRepository.findById("admin-device-id");
        assertThat(adminDevice).isPresent();
    }

    @Test
    @DisplayName("Should return 403 Forbidden when normal user tries to delete a device")
    void DeleteDevice_ShouldReturn403_WhenUserIsNotAdmin() {
        // Arrange
        User user = createUser("normalUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "normal-device-id", "Normal Phone", false);
        createDevice(user, "target-device-id", "Other Phone", false);

        // Act
        HttpResponse<ErrorDto> response = HttpClient.delete("/devices/target-device-id", "normal-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_FORBIDDEN.message());
        
        assertThat(deviceRepository.findById("target-device-id")).isPresent();
    }

    @Test
    @DisplayName("Should return 403 Forbidden when device tries to delete itself")
    void DeleteDevice_ShouldReturn403_WhenDeletingSelf() {
        // Arrange
        User user = createUser("suicideUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "admin-device-id", "Admin Phone", true);

        HttpResponse<ErrorDto> response = HttpClient.delete("/devices/admin-device-id", "admin-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_FORBIDDEN.message());
        
        assertThat(deviceRepository.findById("admin-device-id")).isPresent();
    }

    @Test
    @DisplayName("Should return 404 Not Found when target device does not exist")
    void DeleteDevice_ShouldReturn404_WhenTargetDeviceNotFound() {
        // Arrange
        User user = createUser("searchUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "admin-device-id", "Admin Phone", true);

        // Act
        HttpResponse<ErrorDto> response = HttpClient.delete("/devices/non-existent-id", "admin-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_NOT_FOUND.message());
    }

    @Test
    @DisplayName("Should return 404 Not Found when requesting device (header) does not exist")
    void DeleteDevice_ShouldReturn404_WhenCurrentDeviceNotFound() {
        // Arrange
        User user = createUser("ghostUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        createDevice(user, "target-device-id", "Target Phone", false);

        HttpResponse<ErrorDto> response = HttpClient.delete("/devices/target-device-id", "ghost-device-id", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().message()).isEqualTo(Errors.DEVICE_NOT_FOUND.message());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when Device-Id header is missing")
    void DeleteDevice_ShouldReturn400_WhenHeaderMissing() {
        // Arrange
        User user = createUser("headerlessUser");
        String authToken = tokenService.generateToken(user);
        HttpClient.setAuthToken(authToken);

        HttpResponse<ErrorDto> response = HttpClient.delete("/devices/any-id", null, ErrorDto.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().message()).contains("Required request header 'Device-Id' is missing");
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when user is not logged in")
    void DeleteDevice_ShouldReturn401_WhenNotAuthenticated() {
        // Arrange
        HttpClient.setAuthToken("");

        // Act
        HttpResponse<ErrorDto> response = HttpClient.delete("/devices/any-id", "any-device", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
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
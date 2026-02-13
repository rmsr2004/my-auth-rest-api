package com.myauth.IntegrationTests.Features;

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
import com.myauth.features.Device.adddevice.AddDeviceRequest;
import com.myauth.features.Device.adddevice.AddDeviceResponse;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Add Device Integration Tests")
class AddDeviceTests {
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
    @SuppressWarnings("unused")
    void setup() {
        deviceRepository.deleteAll();
        userRepository.deleteAll();

        HttpClient.setServerAddress("http://localhost:" + port + "/api/auth");
        HttpClient.setAuthToken("");
    }

    @Test
    @DisplayName("Should create Admin Device when database is empty")
    void AddDevice_ShouldCreateAdmin_WhenFirstDevice() {
        // Arrange
        User user = createUser("user1");
        authenticateUser(user);

        AddDeviceRequest request = new AddDeviceRequest("device-1", "Admin Phone");

        // Act
        HttpResponse<AddDeviceResponse> response = HttpClient.post("/devices", request, AddDeviceResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        AddDeviceResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.id()).isEqualTo("device-1");
        assertThat(result.name()).isEqualTo("Admin Phone");
        assertThat(result.message()).isEqualTo("Device created successfully");

        Device createdDevice = deviceRepository.findById("device-1").orElse(null);
        assertThat(createdDevice).isNotNull();
        assertThat(createdDevice.getUser().getUsername()).isEqualTo("user1");
        assertThat(createdDevice.getIsAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should create Normal Device when devices already exist")
    void AddDevice_ShouldCreateNormal_WhenNotFirstDevice() {
        // Arrange
        User existingUser = createUser("existing");
        Device existingDevice = new Device();
        
        existingDevice.setId("existing-id");
        existingDevice.setName("Existing Admin");
        existingDevice.setUser(existingUser);
        existingDevice.setIsAdmin(true);
        
        deviceRepository.save(existingDevice);

        authenticateUser(existingUser);

        AddDeviceRequest request = new AddDeviceRequest("device-2", "Normal Phone");

        // Act
        HttpResponse<AddDeviceResponse> response = HttpClient.post("/devices", request, AddDeviceResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        AddDeviceResponse result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.id()).isEqualTo("device-2");
        assertThat(result.name()).isEqualTo("Normal Phone");
        assertThat(result.message()).isEqualTo("Device created successfully");

        Device createdDevice = deviceRepository.findById("device-2").orElse(null);
        assertThat(createdDevice).isNotNull();
        assertThat(createdDevice.getUser().getUsername()).isEqualTo("existing");
        assertThat(createdDevice.getIsAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should return 409 Conflict when device ID already exists for user")
    void AddDevice_ShouldReturn409_WhenDeviceAlreadyExists() {
        // Arrange
        User user = createUser("userRepeat");
        authenticateUser(user);

        String deviceId = "duplicate-id";

        Device existing = new Device();
        existing.setId(deviceId);
        existing.setName("Original");
        existing.setUser(user);
        existing.setIsAdmin(true);
        deviceRepository.save(existing);

        AddDeviceRequest request = new AddDeviceRequest(deviceId, "New Name");

        // Act
        HttpResponse<ErrorDto> response = HttpClient.post("/devices", request, ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

        ErrorDto result = response.body();

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo(Errors.DEVICE_ALREADY_EXISTS.message());
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void AddDevice_ShouldReturn401_WhenNoToken() {
        // Arrange
        HttpClient.setAuthToken("");

        AddDeviceRequest request = new AddDeviceRequest("id", "name");

        // Act
        HttpResponse<ErrorDto> response = HttpClient.post("/devices", request, ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
    
    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        return userRepository.save(user);
    }

    private void authenticateUser(User user) {
        String token = tokenService.generateToken(user);
        HttpClient.setAuthToken(token);
    }
}
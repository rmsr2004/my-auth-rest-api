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
import com.myauth.features.adddevice.AddDeviceRequest;
import com.myauth.features.adddevice.AddDeviceResponse;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.TokenService;

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
        existingDevice.setAdmin(true);
        deviceRepository.save(existingDevice);

        User newUser = createUser("newUser");
        authenticateUser(newUser);

        AddDeviceRequest request = new AddDeviceRequest("device-2", "Normal Phone");

        // Act
        HttpResponse<AddDeviceResponse> response = HttpClient.post("/devices", request, AddDeviceResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Assert DB
        // Como count() > 0, este dispositivo deve ser admin = false
        Device savedDevice = deviceRepository.findById("device-2").orElseThrow();
        assertThat(savedDevice.isAdmin()).isFalse();
    }

    // --- CENÁRIO 3: CONFLITO (409) ---
    @Test
    @DisplayName("Should return 409 Conflict when device ID already exists for user")
    void AddDevice_ShouldReturn409_WhenDeviceAlreadyExists() {
        // Arrange
        User user = createUser("userRepeat");
        authenticateUser(user);

        String deviceId = "duplicate-id";

        // Pré-inserir o device na BD
        Device existing = new Device();
        existing.setId(deviceId);
        existing.setName("Original");
        existing.setUser(user);
        existing.setAdmin(true);
        deviceRepository.save(existing);

        AddDeviceRequest request = new AddDeviceRequest(deviceId, "New Name");

        // Act
        HttpResponse<ErrorDto> response = HttpClient.post("/devices", request, ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        // Ajusta a mensagem ao que tens no Errors.DEVICE_ALREADY_EXISTS
        if (response.body() != null) {
            assertThat(response.body().message()).isNotBlank();
        }
    }

    // --- CENÁRIO 4: NÃO AUTENTICADO (401) ---
    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void AddDevice_ShouldReturn401_WhenNoToken() {
        // Arrange
        HttpClient.setAuthToken(""); // Garante que não há token
        AddDeviceRequest request = new AddDeviceRequest("id", "name");

        // Act
        HttpResponse<Void> response = HttpClient.post("/devices", request, Void.class);

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
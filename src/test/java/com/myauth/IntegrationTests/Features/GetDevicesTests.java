package com.myauth.IntegrationTests.Features;

import java.util.List;

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
import com.myauth.conf.spring.security.TokenService;
import com.myauth.features.getdevices.GetDevicesResponse;
import com.myauth.features.getdevices.GetDevicesResponse.DeviceDto;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IDeviceRepository;
import com.myauth.infrastructure.db.repositories.IUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Get Devices Integration Tests")
class GetDevicesTests {
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
    @DisplayName("Should return 200 with list of devices when user has devices")
    void getDevices_ShouldReturnList_WhenDevicesExist() {
        // Arrange
        User user = createUser("userWithDevices");
        authenticateUser(user);

        createDevice("dev-1", "iPhone 13", true, user);
        createDevice("dev-2", "Pixel 8", false, user);

        // Act
        HttpResponse<GetDevicesResponse> response = HttpClient.get("/devices", GetDevicesResponse.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        GetDevicesResponse body = response.body();
        assertThat(body).isNotNull();
        assertThat(body.devices()).hasSize(2);
        assertThat(body.message()).isEqualTo("Devices successfully retrieved");

        List<DeviceDto> devices = body.devices();
        
        DeviceDto firstDevice = devices.stream().filter(d -> d.id().equals("dev-1")).findFirst().orElseThrow();
        assertThat(firstDevice.name()).isEqualTo("iPhone 13");
        assertThat(firstDevice.isAdmin()).isTrue();

        DeviceDto secondDevice = devices.stream().filter(d -> d.id().equals("dev-2")).findFirst().orElseThrow();
        assertThat(secondDevice.name()).isEqualTo("Pixel 8");
        assertThat(secondDevice.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should return 200 with empty list when user has no devices")
    void getDevices_ShouldReturnEmptyList_WhenNoDevices() {
        // Arrange
        User user = createUser("userEmpty");
        authenticateUser(user);

        // Act
        HttpResponse<GetDevicesResponse> response = HttpClient.get("/devices", GetDevicesResponse.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        GetDevicesResponse body = response.body();
        assertThat(body).isNotNull();
        assertThat(body.devices()).isEmpty();
        assertThat(body.message()).isEqualTo("Devices successfully retrieved");
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void getDevices_ShouldReturn401_WhenNoToken() {
        // Arrange
        HttpClient.setAuthToken("");

        // Act
        HttpResponse<ErrorDto> response = HttpClient.get("/devices", ErrorDto.class);

        // Assert
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.body().message()).isEqualTo("Authentication is required to access this resource");
    }

    // --- Helpers ---

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pass123");
        return userRepository.save(user);
    }

    private void authenticateUser(User user) {
        String token = tokenService.generateToken(user);
        HttpClient.setAuthToken(token);
    }

    private void createDevice(String id, String name, boolean isAdmin, User user) {
        Device device = new Device();
        device.setId(id);
        device.setName(name);
        device.setIsAdmin(isAdmin);
        device.setUser(user);
        deviceRepository.save(device);
    }
}
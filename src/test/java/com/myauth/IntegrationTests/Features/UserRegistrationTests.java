package com.myauth.IntegrationTests.Features;

import com.myauth.Api.Features.UserRegistration.RegisterRequestDto;
import com.myauth.Api.Features.UserRegistration.RegisterResponseDto;
import com.myauth.Domain.Shared.ErrorDto;
import com.myauth.IntegrationTests.Configuration.Containers.PostgreSQLTestContainer;
import com.myauth.IntegrationTests.Utils.Requests.HttpClient;
import com.myauth.IntegrationTests.Utils.Requests.HttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Registration Integration Tests")
class UserRegistrationTests extends PostgreSQLTestContainer {
    @BeforeAll
    static void setupHttpAddress() {
        HttpClient.setServerAddress("http://localhost:8080/api/auth");
    }

    @Test
    @DisplayName("Test to check if container was created and is running")
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Should return user details when user is valid")
    void UserRegistration_ShouldReturn201_WhenRequestIsValid() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("username", "password");

        // Act
        HttpResponse<RegisterResponseDto> response = HttpClient.post("/register", request, RegisterResponseDto.class);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        RegisterResponseDto result = response.body();

        assertThat(result.id()).isEqualTo("1");
        assertThat(result.username()).isEqualTo("username");
        assertThat(result.message()).isEqualTo("User successfully registered!");
    }

    @Test
    @DisplayName("Should return error message when user already exists")
    void UserRegistration_ShouldReturn409_WhenRequestIsInvalid() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("username", "password");

        // Act (1st post)
        HttpResponse<RegisterResponseDto> firstResponse = HttpClient.post("/register", request, RegisterResponseDto.class);

        // Assert (1st post)
        assertThat(firstResponse).isNotNull();
        assertThat(firstResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Act (2nd post)
        HttpResponse<ErrorDto> secondResponse = HttpClient.post("/register", request, ErrorDto.class);

        // Assert (2nd post)
        assertThat(secondResponse).isNotNull();
        assertThat(secondResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

        ErrorDto result = secondResponse.body();
        assertThat(result.error()).isEqualTo("Conflict");
        assertThat(result.message()).isEqualTo("User already exists!");
    }
}

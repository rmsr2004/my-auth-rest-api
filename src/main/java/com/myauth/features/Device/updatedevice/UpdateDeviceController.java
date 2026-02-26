package com.myauth.features.Device.updatedevice;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.config.versioning.ApiVersion;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.shared.result.ErrorDto;
import com.myauth.shared.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name="Device Management", description="Endpoints for managing user devices")
@AllArgsConstructor
@RestController
@RequestMapping("/devices")
public class UpdateDeviceController {
    private final UpdateDeviceHandler handler;

    @Operation(summary="Updates a device for a specific user")
    @Parameter(name="deviceId", description="Unique identifier of the device to be updated", example="device123", required=true)
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="Device updated successfully", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=UpdateDeviceResponse.class)
        )),
        @ApiResponse(responseCode="404", description="Device not found", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
    @ApiVersion("1")
    @PutMapping("/{deviceId}")
    public ResponseEntity<?> updateDevice(
        @AuthenticationPrincipal User user,
        @PathVariable String deviceId,
        @Parameter(
            name = "Device-Id", 
            description = "Unique identifier for the device making the request", 
            example = "device-123-abc", 
            required = true,
            in = ParameterIn.HEADER
        )
        @RequestHeader("Device-Id") String currentDeviceId,
        @RequestBody @Valid UpdateDeviceRequest body,
        HttpServletRequest request
    ) {
        Device device = body.toEntity();

        Result<Device> result = handler.updateDeviceForUser(deviceId, user, currentDeviceId, device);

        if (result.isFailure()) {
            log.warn("Failed to update device with ID: {} for user: {} | Reason: {}", deviceId, user.getUsername(), result.getError());
            return ResponseEntity.status(result.getError().code()).body(
                new ErrorDto(
                    OffsetDateTime.now().toString(),
                    result.getError().code().value(),
                    result.getError().code().getReasonPhrase(),
                    result.getError().message(),
                    request.getRequestURI()
                )
            );
        }

        log.info("Device with ID: {} updated successfully for user: {}", deviceId, user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
            new UpdateDeviceResponse(
                result.getValue().getId(),
                result.getValue().getName(),
                result.getValue().getIsAdmin()
            )
        );
    }
}

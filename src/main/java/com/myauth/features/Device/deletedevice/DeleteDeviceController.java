package com.myauth.features.Device.deletedevice;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name="Device Management", description="Endpoints for managing user devices")
@AllArgsConstructor
@RestController
@RequestMapping("api/auth/devices")
public class DeleteDeviceController {
    private final DeleteDeviceHandler handler;

    @Operation(summary="Deletes a device for a specific user")
    @Parameter(name="deviceId", description="Unique identifier of the device to be deleted", example="device123", required=true)
    @ApiResponses(value={
        @ApiResponse(responseCode="204", description="User successfully deleted a device", content=@Content()),
        @ApiResponse(responseCode="404", description="Device not found", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<?> deleteDevice(
        @AuthenticationPrincipal User user, 
        HttpServletRequest request, 
        @PathVariable String deviceId,
        @Parameter(
            name = "Device-Id", 
            description = "Unique identifier for the device making the request", 
            example = "device-123-abc", 
            required = true,
            in = ParameterIn.HEADER
        )
        @RequestHeader("Device-Id") String currentDeviceId
    ) {
        Result<Void> result = handler.deleteDeviceForUser(deviceId, user.getId(), currentDeviceId);

        if (result.isFailure()) {
            log.warn("Failed to delete device with ID: {} for user: {} | Reason: {}", deviceId, user.getUsername(), result.getError());
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

        log.info("Device with ID: {} successfully deleted for user: {}", deviceId, user.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.myauth.features.Device.getdevices;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.config.versioning.ApiVersion;
import com.myauth.features.Device.getdevices.GetDevicesResponse.DeviceDto;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.shared.result.ErrorDto;
import com.myauth.shared.result.Result;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/devices")
public class GetDevicesController {
    private final GetDevicesHandler handler;

    @Operation(summary="Gets devices for a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="User successfully retrieved devices", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=GetDevicesResponse.class)
        ))
    })
    @ApiVersion("1")
    @GetMapping
    public ResponseEntity<?> getDevices(@AuthenticationPrincipal User user, HttpServletRequest request) {
        Result<List<DeviceDto>> result = handler.getDevicesForUser(user);

        if (result.isFailure()) {
            log.warn("Failed to retrieve devices for user: {} | Reason: {}", user.getUsername(), result.getError());
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

        log.info("Devices successfully retrieved for user: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
            new GetDevicesResponse(
                result.getValue(),
                "Devices successfully retrieved"
            )
        );
    }
}

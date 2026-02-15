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

import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/auth/devices")
public class UpdateDeviceController {
    private final UpdateDeviceHandler handler;

    @PutMapping("/{deviceId}")
    public ResponseEntity<?> updateDevice(
        @AuthenticationPrincipal User user,
        @PathVariable String deviceId,
        @RequestHeader("Device-Id") String currentDeviceId,
        @RequestBody @Valid UpdateDeviceRequest body,
        HttpServletRequest request
    ) {
        Result<Device> result = handler.updateDeviceForUser(deviceId, user, currentDeviceId, body);

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

package com.myauth.features.deletedevice;

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

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth/devices")
public class DeleteDeviceController {
    private final DeleteDeviceHandler handler;

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<?> deleteDevice(@AuthenticationPrincipal User user, HttpServletRequest request, @PathVariable String deviceId, @RequestHeader("Device-Id") String currentDeviceId) {
        Result<Void> result = handler.deleteDeviceForUser(deviceId, user.getId(), currentDeviceId);

        if (result.isFailure()) {
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

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

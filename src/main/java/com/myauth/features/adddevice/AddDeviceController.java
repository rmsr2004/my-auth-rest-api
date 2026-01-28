package com.myauth.features.adddevice;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.Device;
import com.myauth.infrastructure.db.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/auth/devices")
@AllArgsConstructor
public class AddDeviceController {
    private final AddDeviceHandler handler;
    
    @Operation(summary="Creates a device for a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="201", description="User successfully created a device", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=AddDeviceResponse.class)
        )),
        @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="401", description="Unauthorized", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="409", description="Conflict", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        )),
        @ApiResponse(responseCode="500", description="Internal Server Error", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
    @PostMapping
    public ResponseEntity<?> addDevice(@Valid @RequestBody AddDeviceRequest body, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Result<Device> result = handler.addDevice(user, body.id(), body.name());

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
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new AddDeviceResponse(
                result.getValue().getId(), 
                result.getValue().getName(), 
                "Device created successfully"
            )
        );
    }
}

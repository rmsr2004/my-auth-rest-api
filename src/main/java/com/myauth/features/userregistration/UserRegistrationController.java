package com.myauth.features.userregistration;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth/register")
public class UserRegistrationController {
    public final UserRegistrationHandler handler;

    @Operation(summary="Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode="201", description="User successfully registered", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = RegisterResponseDto.class)
        )),
        @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="409", description="User are already registered", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="500", description="Internal Server Error", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
    @PostMapping()
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto body, HttpServletRequest request) {
        User user = body.toUser();
        
        Result<User> result = handler.register(user);
        
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
            new RegisterResponseDto(
                result.getValue().getId().toString(),
                result.getValue().getUsername(),
                "User successfully registered!"
            )
        );
    }
}

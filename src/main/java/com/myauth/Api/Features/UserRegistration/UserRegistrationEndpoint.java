package com.myauth.Api.Features.UserRegistration;

import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Shared.ErrorDto;
import com.myauth.Domain.Shared.Result;
import com.myauth.Domain.Services.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/auth/register")
public class UserRegistrationEndpoint {
    public final IAuthService authService;

    public UserRegistrationEndpoint(IAuthService authService) {
        this.authService = authService;
    }

    @Operation(summary="Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode="201", description="User successfully registered", content=@Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = RegisterResponseDto.class)
            )),
            @ApiResponse(responseCode="409", description="User are already registered", content=@Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorDto.class)
            )),
            @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
                    mediaType="application/json",
                    schema=@Schema(implementation = ErrorDto.class)
            ))
    })
    @PostMapping()
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto body, HttpServletRequest request) {
        User user = body.toDomain();
        Result<User> result = authService.register(user);
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new RegisterResponseDto(
                            result.getValue().getId().toString(),
                            result.getValue().getUsername(),
                            "User successfully registered!"
                    )
            );
        }

        return ResponseEntity.status(result.getError().code()).body(
                new ErrorDto(
                        OffsetDateTime.now().toString(),
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        result.getError().message(),
                        request.getRequestURI()
                )
        );
    }
}

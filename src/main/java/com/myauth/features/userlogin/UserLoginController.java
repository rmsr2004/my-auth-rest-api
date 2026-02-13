package com.myauth.features.userlogin;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/auth/login")
public class UserLoginController {
    public final UserLoginHandler handler;

    @Operation(summary="User login")
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="User successfully logged in", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=LoginResponseDto.class)
        )),
        @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="403", description="User credentials are invalid", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        )),
        @ApiResponse(responseCode="404", description="User not found", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        )),
        @ApiResponse(responseCode="500", description="Internal Server Error", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
    @PutMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto body, HttpServletRequest request) {
        User user = body.toUser();
        
        Result<String> result = handler.login(user);

        if (result.isFailure()) {
            log.warn("Login failed for user: {} | Reason: {}", user.getUsername(), result.getError());
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
        
        log.info("Login successful for user: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
            new LoginResponseDto(
                result.getValue(),
                "User { " + user.getUsername() + " } successfully logged in!"
            )
        );
    }
}
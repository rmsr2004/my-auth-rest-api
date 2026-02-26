package com.myauth.features.Secret.addsecret;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.config.versioning.ApiVersion;
import com.myauth.infrastructure.db.entities.Secret;
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
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name="Secrets", description="Endpoints for managing user secrets")
@AllArgsConstructor
@RestController
@RequestMapping("/secrets")
public class AddSecretController {
    private final AddSecretHandler handler;

    @Operation(summary="Adds a secret to a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="User successfully created a secret", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=AddSecretResponse.class)
        )),
        @ApiResponse(responseCode="409", description="Secret already exists for this user", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        ))
    })
    @ApiVersion("1")
    @PostMapping
    public ResponseEntity<?> addSecret(@Valid @RequestBody AddSecretRequest body, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Secret secret = body.toEntity();

        Result<Secret> result = handler.addSecret(user, secret);

        if (result.isFailure()) {
            log.warn("Failed to add secret for user: {} | Reason: {}", user.getUsername(), result.getError());
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
        
        log.info("Secret with ID: {} successfully added for user: {}", result.getValue().getId(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new AddSecretResponse(
                result.getValue().getId(),
                result.getValue().getIssuer(),
                "Secret successfully created"
            )
        );
    }
}

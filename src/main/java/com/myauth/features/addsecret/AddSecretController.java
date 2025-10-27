package com.myauth.features.addsecret;

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
import com.myauth.infrastructure.db.entities.Secret;
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
@RequestMapping("api/auth/secret")
public class AddSecretController {
    private final AddSecretHandler handler;

    @Operation(summary="Adds a secret to a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="User successfully created a secret", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=AddSecretResponseDto.class)
        )),
        @ApiResponse(responseCode="409", description="Conflict", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        )),
        @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
    @PostMapping
    public ResponseEntity<?> addSecret(@Valid @RequestBody AddSecretRequestDto body, @AuthenticationPrincipal User user , HttpServletRequest request) {
        String secret = body.secret();
        String issuer = body.issuer();

        Result<Secret> result = handler.addSecret(user, secret, issuer);

        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new AddSecretResponseDto(
                    result.getValue().getId(),
                    result.getValue().getIssuer(),
                    "Secret successfully created"
                )
            );
        }
        
        return ResponseEntity.status(result.getError().code()).body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                result.getError().code().value(),
                "Conflict",
                result.getError().message(),
                request.getRequestURI()
            )
        );
    }
}

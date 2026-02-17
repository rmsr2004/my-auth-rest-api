package com.myauth.features.Secret.getsecrets;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.config.versioning.ApiVersion;
import com.myauth.features.Secret.getsecrets.GetSecretsResponse.SecretDto;
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
@Tag(name="Secrets", description="Endpoints for managing user secrets")
@AllArgsConstructor
@RestController
@RequestMapping("/secrets")
public class GetSecretsController {
    private final GetSecretsHandler handler;

    @Operation(summary="Gets secrets for a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="Secrets successfully retrieved", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=GetSecretsResponse.class)
        ))
    })
    @ApiVersion("1")
    @GetMapping
    public ResponseEntity<?> getSecrets(@AuthenticationPrincipal User user, HttpServletRequest request) {
        Result<List<SecretDto>> result = handler.getSecretsForUser(user);

        if (result.isFailure()) {
            log.warn("Failed to retrieve secrets for user: {} | Reason: {}", user.getUsername(), result.getError());
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
        
        log.info("Secrets successfully retrieved for user: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
            new GetSecretsResponse(
                result.getValue(),
                "Secrets successfully retrieved"
            )
        );
    }
}

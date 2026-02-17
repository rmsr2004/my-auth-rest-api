package com.myauth.features.Secret.deletesecret;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.config.versioning.ApiVersion;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.shared.result.ErrorDto;
import com.myauth.shared.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class DeleteSecretController {
    private final DeleteSecretHandler handler;

    @Operation(summary="Deletes a secret for a specific user")
    @Parameter(name="secretId", description="Unique identifier of the secret to be deleted", example="1", required=true)
    @ApiResponses(value={
        @ApiResponse(responseCode="204", description="User successfully deleted a secret", content=@Content()),
        @ApiResponse(responseCode="404", description="Secret not found", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        )),
    })
    @ApiVersion("1")
    @DeleteMapping("/{secretId}")
    public ResponseEntity<?> deleteSecret(@AuthenticationPrincipal User user, HttpServletRequest request, @PathVariable Long secretId) {
        Result<Void> result = handler.deleteSecretForUser(secretId, user.getId());

        if (result.isFailure()) {
            log.warn("Failed to delete secret with ID: {} for user: {} | Reason: {}", secretId, user.getUsername(), result.getError());
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

        log.info("Secret with ID: {} successfully deleted for user: {}", secretId, user.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

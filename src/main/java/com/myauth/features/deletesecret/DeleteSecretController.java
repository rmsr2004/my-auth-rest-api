package com.myauth.features.deletesecret;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myauth.common.utils.ErrorDto;
import com.myauth.common.utils.Result;
import com.myauth.features.addsecret.AddSecretResponseDto;
import com.myauth.infrastructure.db.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/auth/secrets")
public class DeleteSecretController {
    private final DeleteSecretHandler handler;

    @Operation(summary="Deletes a secret for a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="204", description="User successfully deleted a secret", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=AddSecretResponseDto.class)
        )),
        @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="403", description="Forbidden", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="500", description="Internal Server Error", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        ))
    })
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

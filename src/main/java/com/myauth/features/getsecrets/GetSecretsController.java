package com.myauth.features.getsecrets;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth/secrets")
public class GetSecretsController {
    private final GetSecretsHandler handler;

    @Operation(summary="Gets secrets for a specific user")
    @ApiResponses(value={
        @ApiResponse(responseCode="200", description="User successfully retrieved secrets", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=GetSecretsResponseDto.class)
        )),
        @ApiResponse(responseCode="400", description="Bad Request", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="401", description="Unauthorized", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode="500", description="Internal Server Error", content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=ErrorDto.class)
        ))
    })
    @GetMapping
    public ResponseEntity<?> getCodes(@AuthenticationPrincipal User user, HttpServletRequest request) {
        Result<List<SecretDto>> result = handler.getSecretsForUser(user);

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

        return ResponseEntity.status(HttpStatus.OK).body(
            new GetSecretsResponseDto(
                result.getValue(),
                "Codes successfully retrieved"
            )
        );
    }
}

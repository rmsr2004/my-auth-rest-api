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
import com.myauth.infrastructure.db.entities.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth/secrets")
public class DeleteSecretController {
    private final DeleteSecretHandler handler;

    @DeleteMapping("/{secretId}")
    public ResponseEntity<?> deleteSecret(@AuthenticationPrincipal User user, HttpServletRequest request, @PathVariable Long secretId) {
        Result<Void> result = handler.deleteSecretForUser(secretId, user.getId());

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

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

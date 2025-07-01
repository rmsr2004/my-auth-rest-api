package com.myauth.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.myauth.api.entities.User;
import com.myauth.api.entities.Secret;
import com.myauth.api.repositories.SecretRepository;
import com.myauth.api.dtos.secret.SecretRequestDto;
import com.myauth.api.dtos.secret.SecretResponseDto;

@RestController
@RequestMapping("/api/auth/secrets")
public class SecretController {
    private final SecretRepository secretRepository;

    public  SecretController(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    @PostMapping
    public ResponseEntity<?> addSecret(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SecretRequestDto body
    ) {
        Secret secret = new Secret();
        secret.setUser(user);
        secret.setIssuer(body.issuer());
        secret.setSecret(body.secret());

        Long id = secretRepository.save(secret).getId();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SecretResponseDto(id, "Secret added successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSecret(@AuthenticationPrincipal User user, @PathVariable Long id) {
        secretRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new SecretResponseDto(id, "Secret deleted successfully")
        );
    }
}

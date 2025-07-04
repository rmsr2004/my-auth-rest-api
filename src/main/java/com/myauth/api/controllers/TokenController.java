package com.myauth.api.controllers;

import com.myauth.api.dtos.secret.SecretListDto;
import com.myauth.api.dtos.token.TokenListDto;
import com.myauth.api.entities.User;
import com.myauth.api.repositories.SecretRepository;
import com.myauth.api.services.TotpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth/tokens")
public class TokenController {
    private final SecretRepository secretRepository;
    private final TotpService totp;

    public TokenController(SecretRepository secretRepository, TotpService totp) {
        this.secretRepository = secretRepository;
        this.totp = totp;
    }

    @GetMapping
    public ResponseEntity<?> generateTokens(@AuthenticationPrincipal User user) {
        List<SecretListDto> secrets = secretRepository.findByUserId(user.getId())
                .stream()
                .map(secret ->
                        new SecretListDto(
                                secret.getId(),
                                secret.getIssuer(),
                                totp.generateToken(secret.getSecret())
                        )
                )
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                new TokenListDto(secrets)
        );
    }
}

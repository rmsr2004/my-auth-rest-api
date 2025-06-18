package com.myauth.api.controller;

import java.util.Optional;

import com.myauth.api.dto.login.LoginRequestDto;
import com.myauth.api.dto.login.LoginResponseDto;
import com.myauth.api.dto.register.RegisterRequestDto;
import com.myauth.api.dto.register.RegisterResponseDto;
import com.myauth.api.exception.custom.UserAlreadyExistsException;
import com.myauth.api.exception.custom.UserUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.myauth.api.exception.custom.UserNotFoundException;
import com.myauth.api.model.User;
import com.myauth.api.repository.UserRepository;
import com.myauth.api.security.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final TokenService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger;

    public AuthController(
            UserRepository userRepository,
            TokenService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.logger = LoggerFactory.getLogger(AuthController.class);
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto body) throws UserNotFoundException, UserUnauthorizedException {
        logger.debug("Login Request {}", body);

        Optional<User> user = userRepository.findByUsername(body.username());

        if (user.isEmpty()) {
            logger.warn("User tried to login with incorrect username");
            throw new UserNotFoundException("User not found");
        }

        String token;

        if (passwordEncoder.matches(body.password(), user.get().getPassword())) {
            token = jwtService.generateToken(body.username());
            return ResponseEntity.ok(new LoginResponseDto(token));
        } else {
            throw new UserUnauthorizedException("Incorrect credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto body) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(body.username()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(body.username());
        user.setPassword(passwordEncoder.encode(body.password()));
        Long id = userRepository.save(user).getId();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterResponseDto(
                        id,
                        body.username(),
                        "User successfully registered"
                )
        );
    }
}
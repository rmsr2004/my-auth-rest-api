package com.myauth.api.controllers;

import java.util.Optional;

import com.myauth.api.dtos.login.LoginRequestDto;
import com.myauth.api.dtos.login.LoginResponseDto;
import com.myauth.api.dtos.register.RegisterRequestDto;
import com.myauth.api.dtos.register.RegisterResponseDto;
import com.myauth.api.exceptions.custom.UserAlreadyExistsException;
import com.myauth.api.exceptions.custom.UserUnauthorizedException;
import com.myauth.api.entities.Device;
import com.myauth.api.repositories.DeviceRepository;
import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.myauth.api.exceptions.custom.UserNotFoundException;
import com.myauth.api.entities.User;
import com.myauth.api.repositories.UserRepository;
import com.myauth.api.security.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final TokenService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger;

    public AuthController(
            UserRepository userRepository,
            DeviceRepository deviceRepository,
            TokenService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.logger = LoggerFactory.getLogger(AuthController.class);
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto body) throws UserNotFoundException, UserUnauthorizedException, ExecutionControl.NotImplementedException {
        logger.debug("Login Request {}", body);

        Optional<User> user = userRepository.findByUsername(body.username());

        if (user.isEmpty()) {
            logger.warn("User tried to login with incorrect username");
            throw new UserNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(body.password(), user.get().getPassword())) {
            throw new UserUnauthorizedException("Wrong password");
        }

        Device device = new Device();
        device.setUser(user.get());

        String deviceId = deviceRepository.save(device).getId();

        String token = jwtService.generateToken(user.get().getId(), deviceId);

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(token));
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
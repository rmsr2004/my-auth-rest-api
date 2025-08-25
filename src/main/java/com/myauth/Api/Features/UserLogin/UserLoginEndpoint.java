package com.myauth.Api.Features.UserLogin;

import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Services.IAuthService;
import com.myauth.Domain.Shared.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth/login")
public class UserLoginEndpoint {
    public final IAuthService authService;

    public UserLoginEndpoint(IAuthService authService) {
        this.authService = authService;
    }

    @PutMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto body, HttpServletRequest request) {
        User user = body.toDomain();
        Result<User> result = authService.login(user);
    }
}

package com.myauth.features.User.userlogin;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.infrastructure.security.TokenService;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserLoginHandler {
    private final IUserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public Result<String> login(User user) {
        Optional<User> userEntity = userRepository.findByUsername(user.getUsername());

        if (userEntity.isEmpty()) {
            return Result.failure(Errors.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(user.getPassword(), userEntity.get().getPassword())) {
            return Result.failure(Errors.USER_UNAUTHORIZED);
        }

        // Return jwt token
        String token = tokenService.generateToken(userEntity.get());

        return Result.success(token);
    }
}
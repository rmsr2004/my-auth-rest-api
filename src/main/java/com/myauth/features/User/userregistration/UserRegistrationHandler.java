package com.myauth.features.User.userregistration;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;
import com.myauth.shared.result.Errors;
import com.myauth.shared.result.Result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserRegistrationHandler {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Result<User> register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return Result.failure(Errors.USER_ALREADY_EXISTS);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return Result.success(user);
    }
}

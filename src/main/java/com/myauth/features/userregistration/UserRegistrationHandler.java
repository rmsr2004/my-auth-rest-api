package com.myauth.features.userregistration;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myauth.common.utils.Errors;
import com.myauth.common.utils.Result;
import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;

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

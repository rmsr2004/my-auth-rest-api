package com.myauth.Api.Services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Services.IAuthService;
import com.myauth.Domain.Shared.Errors;
import com.myauth.Domain.Shared.Result;
import com.myauth.Infrastructure.Repositories.Entities.UserEntity;
import com.myauth.Infrastructure.Repositories.IUserRepository;
import com.myauth.Infrastructure.Repositories.Mappers.UserMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<User> register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return Result.failure(Errors.USER_ALREADY_EXISTS);
        }

        UserEntity userEntity = userMapper.toEntity(user);

        UserEntity savedUser = userRepository.save(userEntity);

        return Result.success(savedUser.toDomain());
    }

    @Override
    public Result<String> login(User user) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(user.getUsername());

        if (userEntity.isEmpty()) {
            return Result.failure(Errors.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(user.getPassword(), userEntity.get().getPassword())) {
            return Result.failure(Errors.USER_UNAUTHORIZED);
        }

        // Return jwt token
        String token = tokenService.generateToken(userEntity.get().toDomain());

        return Result.success(token);
    }
}

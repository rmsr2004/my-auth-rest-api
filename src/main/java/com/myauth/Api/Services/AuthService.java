package com.myauth.Api.Services;

import com.myauth.Domain.Shared.Errors;
import com.myauth.Infrastructure.Repositories.Entities.UserEntity;
import com.myauth.Infrastructure.Repositories.Mappers.UserMapper;
import org.springframework.stereotype.Service;

import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Shared.Result;
import com.myauth.Domain.Services.IAuthService;
import com.myauth.Infrastructure.Repositories.IUserRepository;

@Service
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    public AuthService(IUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Result<User> register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return Result.failure(Errors.USER_ALREADY_EXISTS);
        }

        UserEntity userEntity = userMapper.toEntity(user);

        UserEntity savedUser = userRepository.save(userEntity);

        return Result.success(savedUser.toDomain());
    }
}

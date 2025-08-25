package com.myauth.Api.Services;

import com.myauth.Domain.Entities.Device;
import com.myauth.Domain.Shared.Errors;
import com.myauth.Infrastructure.Repositories.Entities.UserEntity;
import com.myauth.Infrastructure.Repositories.IDeviceRepository;
import com.myauth.Infrastructure.Repositories.Mappers.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Shared.Result;
import com.myauth.Domain.Services.IAuthService;
import com.myauth.Infrastructure.Repositories.IUserRepository;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final IDeviceRepository deviceRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(IUserRepository userRepository, IDeviceRepository deviceRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    public Result<User> login(User user) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(user.getUsername());

        if (userEntity.isPresent()) {
            return Result.failure(Errors.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(user.getPassword(), userEntity.get().getPassword())) {
            return Result.failure(Errors.USER_UNAUTHORIZED);
        }

        Device device = new Device();

        return null;
    }
}

package com.myauth.Domain.Services;

import com.myauth.Domain.Entities.User;
import com.myauth.Domain.Shared.Result;

public interface IAuthService {
    Result<User> register(User user);
}

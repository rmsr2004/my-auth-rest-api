package com.myauth.api.security;

import io.jsonwebtoken.Claims;

public record TokenValidation(boolean isValid, String message, Claims claims) {}

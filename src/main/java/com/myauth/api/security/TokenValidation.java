package com.myauth.api.security;

public record TokenValidation(boolean isValid, String message) {}

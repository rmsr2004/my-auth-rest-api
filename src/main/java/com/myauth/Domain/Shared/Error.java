package com.myauth.Domain.Shared;

import org.springframework.http.HttpStatus;

public record Error(HttpStatus code, String message) {}

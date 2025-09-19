package com.myauth.common.utils;

import org.springframework.http.HttpStatus;

public record Error(HttpStatus code, String message) {}

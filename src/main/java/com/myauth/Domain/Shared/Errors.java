package com.myauth.Domain.Shared;

import org.springframework.http.HttpStatus;

public class Errors {
    public static final Error USER_ALREADY_EXISTS = new Error(HttpStatus.CONFLICT, "User already exists!");
    public static final Error USER_NOT_FOUND = new Error(HttpStatus.NOT_FOUND, "User not found!");
    public static final Error USER_UNAUTHORIZED = new Error(HttpStatus.UNAUTHORIZED, "User not authorized!");
}

package com.myauth.common.utils;

import org.springframework.http.HttpStatus;

public class Errors {
    public static final Error USER_ALREADY_EXISTS = new Error(HttpStatus.CONFLICT, "User already exists!");
    public static final Error USER_NOT_FOUND = new Error(HttpStatus.NOT_FOUND, "User not found!");
    public static final Error USER_UNAUTHORIZED = new Error(HttpStatus.UNAUTHORIZED, "User not authorized!");
    public static final Error ISSUER_ALREADY_EXISTS = new Error(HttpStatus.CONFLICT, "Issuer already exists!");
    public static final Error SECRET_NOT_FOUND = new Error(HttpStatus.NOT_FOUND, "Secret not found!");
    public static final Error INTERNAL_SERVER_ERROR = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error!");
    public static final Error USER_FORBIDDEN = new Error(HttpStatus.FORBIDDEN, "User is forbidden!");
    public static final Error DEVICE_ALREADY_EXISTS = new Error(HttpStatus.CONFLICT, "Device already exists!");
}

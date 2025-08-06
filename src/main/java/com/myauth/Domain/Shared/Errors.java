package com.myauth.Domain.Shared;

import org.springframework.http.HttpStatus;

public class Errors {
    public static final Error USER_ALREADY_EXISTS = new Error(HttpStatus.CONFLICT, "User already exists!");
}

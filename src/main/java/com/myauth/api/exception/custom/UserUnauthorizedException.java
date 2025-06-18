package com.myauth.api.exception.custom;

public class UserUnauthorizedException extends Exception {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}

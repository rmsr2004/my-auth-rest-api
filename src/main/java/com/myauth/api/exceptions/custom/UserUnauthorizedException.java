package com.myauth.api.exceptions.custom;

public class UserUnauthorizedException extends Exception {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}

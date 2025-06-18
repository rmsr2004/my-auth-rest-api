package com.myauth.api.exception.custom;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        super("User already exists!");
    }
}

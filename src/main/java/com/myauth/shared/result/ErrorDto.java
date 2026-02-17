package com.myauth.shared.result;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}
package com.myauth.api.dto.error;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}

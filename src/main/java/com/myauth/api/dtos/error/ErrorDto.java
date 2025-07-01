package com.myauth.api.dtos.error;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}

package com.myauth.Domain.Shared;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}
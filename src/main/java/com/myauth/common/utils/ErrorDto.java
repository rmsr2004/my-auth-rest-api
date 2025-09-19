package com.myauth.common.utils;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}
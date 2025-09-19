package com.myauth.common.utils;

import lombok.Getter;

@Getter
public class Result<T> {
    private final boolean success;
    private final T value;
    private final Error error;

    public Result(boolean success, T value, Error error) {
        this.success = success;
        this.value = value;
        this.error = error;
    }

    public boolean isFailure() {
        return !this.success;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(true, value, null);
    }

    public static <T> Result<T> failure(Error error) {
        return new Result<>(false, null, error);
    }
}
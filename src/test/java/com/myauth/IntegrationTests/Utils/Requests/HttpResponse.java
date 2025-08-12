package com.myauth.IntegrationTests.Utils.Requests;

public record HttpResponse<T>(int statusCode, T body) {}

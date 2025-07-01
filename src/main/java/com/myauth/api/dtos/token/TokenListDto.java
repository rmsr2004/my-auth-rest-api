package com.myauth.api.dtos.token;

import com.myauth.api.dtos.secret.SecretListDto;

import java.util.List;

public record TokenListDto(List<SecretListDto> tokens) {}

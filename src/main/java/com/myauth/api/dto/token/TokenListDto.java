package com.myauth.api.dto.token;

import com.myauth.api.dto.secret.SecretListDto;

import java.util.List;

public record TokenListDto(List<SecretListDto> tokens) {}

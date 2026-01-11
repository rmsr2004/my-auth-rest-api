package com.myauth.features.getsecrets;

import java.util.List;

public record GetSecretsResponseDto(List<SecretDto> secrets, String message) {};

package com.myauth.infrastructure.security;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myauth.shared.result.ErrorDto;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");


        log.warn("Unauthorized access attempt at {}: {}", request.getRequestURI(), authException.getMessage());
        ErrorDto errorDto = new ErrorDto(
            OffsetDateTime.now().toString(),
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "Authentication is required to access this resource",
            request.getRequestURI()
        );
        
        response.getOutputStream().println(objectMapper.writeValueAsString(errorDto));
    }
}
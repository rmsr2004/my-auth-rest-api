package com.myauth.common.exceptions;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.myauth.common.utils.ErrorDto;
import com.myauth.infrastructure.security.exceptions.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation Error at {}: {}", request.getRequestURI(), errorMessage);

        return ResponseEntity.badRequest().body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        log.warn("User Not Found Error at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
            )
        );
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("No Handler Found Error at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested endpoint does not exist.",
                request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorDto> handleMissingRequestHeaderException(MissingRequestHeaderException ex, HttpServletRequest request) {
        log.warn("Missing Request Header Error at {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Required request header '" + ex.getHeaderName() + "' is missing.",
                request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method Not Allowed Error at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                "The HTTP method '" + ex.getMethod() + "' is not supported for this endpoint.",
                request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Internal Server Error at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ErrorDto(
                OffsetDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred processing your request.",
                request.getRequestURI()
            )
        );
    }
}

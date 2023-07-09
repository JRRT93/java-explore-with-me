package ru.practicum.main.errors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictHandler {
    private static final String REASON = "REQUEST HAS DATA CONFLICT";
    @ExceptionHandler
    public ApiError handleNotFoundException(final DataIntegrityViolationException e) {
        String message = e.getMessage();
        return ApiError.builder()
                .message(message)
                .status(HttpStatus.CONFLICT.name())
                .reason(REASON)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
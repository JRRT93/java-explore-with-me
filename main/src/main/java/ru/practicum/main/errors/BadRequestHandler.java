package ru.practicum.main.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestHandler {
    private static final String REASON_BAD_REQUEST = "Incorrectly made request."; //todo check is important and rewright

    @ExceptionHandler
    public ApiError handleNotFoundException(final MethodArgumentTypeMismatchException e) {
        String message = e.getMessage();
        return ApiError.builder()
                .message(message)
                .reason(REASON_BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
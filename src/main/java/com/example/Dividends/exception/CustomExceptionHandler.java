package com.example.Dividends.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<?> handleCustomException(AbstractException e) {


        return new ResponseEntity<>(ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build(), Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode())));
    }

}

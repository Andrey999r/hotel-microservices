package com.andrey999r.staynova.api.exception;

import com.andrey999r.staynova.api.dto.ExceptionDto;
import com.andrey999r.staynova.general.exceptions.BaseException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ExceptionDto> handleBaseException(BaseException ex) {
    log.warn("Business exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
    return ResponseEntity.status(ex.getStatus())
        .body(
            new ExceptionDto(ex.getMessage(), ex.getClass().getSimpleName(), LocalDateTime.now()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionDto> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");
    log.warn("Validation error: {}", message);
    return ResponseEntity.badRequest()
        .body(new ExceptionDto(message, "ValidationException", LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionDto> handleUnexpected(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return ResponseEntity.internalServerError()
        .body(
            new ExceptionDto(
                "Internal server error", ex.getClass().getSimpleName(), LocalDateTime.now()));
  }
}

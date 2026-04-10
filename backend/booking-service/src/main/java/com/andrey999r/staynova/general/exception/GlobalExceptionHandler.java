package com.andrey999r.staynova.general.exception;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ExceptionDto> handleBaseException(BaseException e) {
    log.warn("Business exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
    return ResponseEntity.status(e.getStatus())
        .body(new ExceptionDto(e.getMessage(), e.getClass().getSimpleName(), LocalDateTime.now()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionDto> handleValidation(MethodArgumentNotValidException e) {
    log.warn("Validation exception: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionDto("Validation error", e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionDto> handleGeneric(Exception e) {
    log.error("Unexpected error [{}]: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionDto("Internal server error", e.getMessage(), LocalDateTime.now()));
  }
}

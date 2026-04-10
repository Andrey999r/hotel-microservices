package com.andrey999r.staynova.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseAuthException.class)
  public ResponseEntity<ExceptionDto> handleBaseAuthException(BaseAuthException e) {
    log.warn("Auth exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
    var body =
        new ExceptionDto(e.getStatus().getReasonPhrase(), e.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(e.getStatus()).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionDto> handleGenericException(Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
    var body = new ExceptionDto("Internal Server Error", e.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}

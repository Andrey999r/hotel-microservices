package com.andrey999r.staynova.infrastructure.adapters.in.web;

import com.andrey999r.staynova.application.services.exceptions.BLException;
import com.andrey999r.staynova.domain.exceptions.DomainException;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.ExceptionDto;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ExceptionDto> handleBaseException(DomainException e) {
    log.warn("Business logic exception in domain layer: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionDto(e.getMessage(), e.getClass().getSimpleName(), LocalDateTime.now()));
  }

  @ExceptionHandler(InfrastructureException.class)
  public ResponseEntity<ExceptionDto> handleBaseException(InfrastructureException e) {
    log.warn("Infrastructure exception: {}", e.getMessage());
    return ResponseEntity.status(e.getStatus())
        .body(new ExceptionDto(e.getMessage(), e.getClass().getSimpleName(), LocalDateTime.now()));
  }

  @ExceptionHandler(BLException.class)
  public ResponseEntity<ExceptionDto> handleBaseException(BLException e) {
    log.warn("Business logic exception in application layer: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionDto(e.getMessage(), e.getClass().getSimpleName(), LocalDateTime.now()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ExceptionDto> handleNotFound(EntityNotFoundException e) {
    log.warn("Entity not found: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ExceptionDto("Not found", e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionDto> handleGeneric(Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionDto("Internal server error", e.getMessage(), LocalDateTime.now()));
  }
}

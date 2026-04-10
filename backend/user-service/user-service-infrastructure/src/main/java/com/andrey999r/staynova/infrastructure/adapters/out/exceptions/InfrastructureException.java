package com.andrey999r.staynova.infrastructure.adapters.out.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class InfrastructureException extends RuntimeException {
  private final HttpStatus status;

  public InfrastructureException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}

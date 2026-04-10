package com.andrey999r.staynova.application.services.exceptions;

public abstract class BLException extends RuntimeException {

  public BLException(String message) {
    super(message);
  }
}

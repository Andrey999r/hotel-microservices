package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class PasswordLengthException extends DomainException {
  public PasswordLengthException(String message) {
    super(message);
  }
}

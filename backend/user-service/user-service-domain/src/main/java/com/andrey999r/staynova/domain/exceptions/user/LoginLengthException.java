package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class LoginLengthException extends DomainException {
  public LoginLengthException(String message) {
    super(message);
  }
}

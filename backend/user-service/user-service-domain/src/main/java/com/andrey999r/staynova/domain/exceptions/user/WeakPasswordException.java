package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class WeakPasswordException extends DomainException {
  public WeakPasswordException(String message) {
    super(message);
  }
}

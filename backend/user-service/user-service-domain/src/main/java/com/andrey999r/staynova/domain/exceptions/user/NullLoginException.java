package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullLoginException extends DomainException {
  public NullLoginException() {
    super("Login is null");
  }
}

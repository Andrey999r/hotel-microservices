package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullPasswordException extends DomainException {
  public NullPasswordException() {
    super("Password is null");
  }
}

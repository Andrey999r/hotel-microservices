package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullEmailException extends DomainException {
  public NullEmailException() {
    super("Email is null");
  }
}

package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullUserIdException extends DomainException {
  public NullUserIdException() {
    super("Null user id");
  }
}

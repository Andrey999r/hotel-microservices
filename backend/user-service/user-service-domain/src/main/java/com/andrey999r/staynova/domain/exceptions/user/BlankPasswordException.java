package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class BlankPasswordException extends DomainException {
  public BlankPasswordException() {
    super("Password is blank");
  }
}

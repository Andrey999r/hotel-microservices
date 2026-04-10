package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class BlankEmailException extends DomainException {
  public BlankEmailException() {
    super("Email is blank");
  }
}

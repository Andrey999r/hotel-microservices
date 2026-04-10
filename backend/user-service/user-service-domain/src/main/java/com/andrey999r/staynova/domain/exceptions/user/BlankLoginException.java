package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class BlankLoginException extends DomainException {
  public BlankLoginException() {
    super("Login is empty or blank");
  }
}

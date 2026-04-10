package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class InvalidFormatEmailException extends DomainException {
  public InvalidFormatEmailException() {
    super("Email format is wrong");
  }
}

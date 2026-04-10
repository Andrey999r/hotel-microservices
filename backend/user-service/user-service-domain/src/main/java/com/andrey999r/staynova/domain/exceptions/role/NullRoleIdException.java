package com.andrey999r.staynova.domain.exceptions.role;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullRoleIdException extends DomainException {

  public NullRoleIdException() {
    super("Role Id is null");
  }
}

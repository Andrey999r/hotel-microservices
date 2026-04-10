package com.andrey999r.staynova.domain.exceptions.role;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullRoleNameException extends DomainException {

  public NullRoleNameException() {
    super("Null role name");
  }
}

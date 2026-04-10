package com.andrey999r.staynova.domain.exceptions.role;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class BlankRoleNameException extends DomainException {
  public BlankRoleNameException() {
    super("Role is blank");
  }
}

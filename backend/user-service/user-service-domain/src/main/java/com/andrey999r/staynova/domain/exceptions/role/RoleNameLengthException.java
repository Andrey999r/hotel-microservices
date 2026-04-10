package com.andrey999r.staynova.domain.exceptions.role;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class RoleNameLengthException extends DomainException {
  public RoleNameLengthException(int length) {
    super(String.format("Length can't be more than %d characters", length));
  }
}

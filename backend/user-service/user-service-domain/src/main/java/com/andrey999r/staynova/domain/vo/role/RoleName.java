package com.andrey999r.staynova.domain.vo.role;

import com.andrey999r.staynova.domain.exceptions.role.BlankRoleNameException;
import com.andrey999r.staynova.domain.exceptions.role.NullRoleNameException;

public record RoleName(String name) {
  static final int MIN_LENGTH = 2;

  public RoleName {
    validateRoleName(name);
  }

  private void validateRoleName(String value) {
    if (value == null) {
      throw new NullRoleNameException();
    }

    if (value.isBlank()) {
      throw new BlankRoleNameException();
    }

    if (value.length() < MIN_LENGTH) {
      throw new BlankRoleNameException();
    }
  }
}

package com.andrey999r.staynova.domain.vo.role;

import com.andrey999r.staynova.domain.exceptions.role.NullRoleIdException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record RoleId(UUID id) {
  public RoleId {
    validateRoleId(id);
  }

  private void validateRoleId(UUID id) {
    ensureNotNull(id);
  }

  private void ensureNotNull(UUID id) {
    if (id == null) {
      throw new NullRoleIdException();
    }
  }
}

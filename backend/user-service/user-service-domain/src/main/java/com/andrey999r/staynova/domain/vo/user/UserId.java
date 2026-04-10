package com.andrey999r.staynova.domain.vo.user;

import com.andrey999r.staynova.domain.exceptions.user.NullUserIdException;
import java.util.UUID;

public record UserId(UUID id) {
  public UserId {
    validateUserId(id);
  }

  private void validateUserId(UUID id) {
    ensureNotNull(id);
  }

  private void ensureNotNull(UUID id) {
    if (id == null) {
      throw new NullUserIdException();
    }
  }
}

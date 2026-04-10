package com.andrey999r.staynova.domain.vo.user;

import com.andrey999r.staynova.domain.exceptions.user.BlankPasswordException;
import com.andrey999r.staynova.domain.exceptions.user.NullPasswordException;
import com.andrey999r.staynova.domain.exceptions.user.PasswordLengthException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Password(String password) {
  static final int MIN_LENGTH = 8;

  public Password {
    ensureNotNull(password);
    ensureNotBlank(password);
    ensureHasEnoughLength(password);
  }

  private static void ensureNotNull(String value) {
    if (value == null) {
      throw new NullPasswordException();
    }
  }

  private static void ensureNotBlank(String value) {
    if (value.isBlank()) {
      throw new BlankPasswordException();
    }
  }

  private static void ensureHasEnoughLength(String value) {
    if (value.length() < MIN_LENGTH) {
      throw new PasswordLengthException(
          String.format("Password needs to be at least %d symbols length", MIN_LENGTH));
    }
  }
}

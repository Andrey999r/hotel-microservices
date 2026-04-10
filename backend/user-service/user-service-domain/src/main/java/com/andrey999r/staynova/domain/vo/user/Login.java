package com.andrey999r.staynova.domain.vo.user;

import com.andrey999r.staynova.domain.exceptions.user.BlankLoginException;
import com.andrey999r.staynova.domain.exceptions.user.LoginLengthException;
import com.andrey999r.staynova.domain.exceptions.user.NullLoginException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Login(String login) {
  static final int MIN_LENGTH = 6;

  public Login {
    validateLogin(login);
  }

  private void validateLogin(String login) {
    ensureNotNull(login);
    ensureNotBlank(login);
    ensureHasEnoughLength(login);
  }

  private void ensureHasEnoughLength(String login) {
    if (login.length() < MIN_LENGTH) {
      throw new LoginLengthException(
          String.format("Login length should be at least %d", MIN_LENGTH));
    }
  }

  private void ensureNotNull(String login) {
    if (login == null) {
      throw new NullLoginException();
    }
  }

  private void ensureNotBlank(String login) {
    if (login.isBlank()) {
      throw new BlankLoginException();
    }
  }
}

package com.andrey999r.staynova.application.services.exceptions;

import com.andrey999r.staynova.domain.vo.user.Login;

public class UserAlreadyExistsException extends BLException {
  public UserAlreadyExistsException(Login login) {
    super("User already exists: " + login.login());
  }
}

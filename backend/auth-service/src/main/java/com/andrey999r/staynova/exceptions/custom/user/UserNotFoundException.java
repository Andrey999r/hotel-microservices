package com.andrey999r.staynova.exceptions.custom.user;

import com.andrey999r.staynova.exceptions.BaseAuthException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseAuthException {

  public UserNotFoundException(String login) {
    super("User not found: " + login, HttpStatus.NOT_FOUND);
  }
}

package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.user;

import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends InfrastructureException {
  public UserNotFoundException(String login) {
    super("User not found: " + login, HttpStatus.NOT_FOUND);
  }
}

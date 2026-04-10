package com.andrey999r.staynova.exceptions.custom.auth;

import com.andrey999r.staynova.exceptions.BaseAuthException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseAuthException {

  public InvalidCredentialsException() {
    super("Invalid login or password", HttpStatus.UNAUTHORIZED);
  }
}

package com.andrey999r.staynova.exceptions.custom.registration;

import com.andrey999r.staynova.exceptions.BaseAuthException;
import org.springframework.http.HttpStatus;

public class RegistrationFailedException extends BaseAuthException {

  public RegistrationFailedException(String reason) {
    super("Registration failed: " + reason, HttpStatus.BAD_GATEWAY);
  }
}

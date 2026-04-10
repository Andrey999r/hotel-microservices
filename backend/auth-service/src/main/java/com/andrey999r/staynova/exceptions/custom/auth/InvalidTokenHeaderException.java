package com.andrey999r.staynova.exceptions.custom.auth;

import com.andrey999r.staynova.exceptions.BaseAuthException;
import org.springframework.http.HttpStatus;

public class InvalidTokenHeaderException extends BaseAuthException {

  public InvalidTokenHeaderException() {
    super("Authorization header is missing or malformed", HttpStatus.BAD_REQUEST);
  }
}

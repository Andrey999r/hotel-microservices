package com.andrey999r.staynova.exception.custom;

import com.andrey999r.staynova.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
  public InvalidTokenException() {
    super("Token signature is invalid", HttpStatus.UNAUTHORIZED);
  }
}
package com.andrey999r.staynova.exception.custom;

import com.andrey999r.staynova.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BlacklistedTokenException extends BaseException {
  public BlacklistedTokenException() {
    super("Token has been revoked", HttpStatus.UNAUTHORIZED);
  }
}

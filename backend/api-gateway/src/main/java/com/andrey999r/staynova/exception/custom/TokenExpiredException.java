package com.andrey999r.staynova.exception.custom;

import com.andrey999r.staynova.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
  public TokenExpiredException() {
    super("Token has expired", HttpStatus.UNAUTHORIZED);
  }
}

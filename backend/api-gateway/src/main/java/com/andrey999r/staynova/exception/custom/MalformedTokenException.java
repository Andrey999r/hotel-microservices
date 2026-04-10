package com.andrey999r.staynova.exception.custom;


import com.andrey999r.staynova.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MalformedTokenException extends BaseException {
  public MalformedTokenException() {
    super("Token is malformed or unsupported", HttpStatus.UNAUTHORIZED);
  }
}
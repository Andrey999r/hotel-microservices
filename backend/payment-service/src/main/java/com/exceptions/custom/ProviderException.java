package com.exceptions.custom;

import com.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ProviderException extends BaseException {

  public ProviderException(String message, Throwable cause) {
    super("Payment provider error: " + message, HttpStatus.BAD_GATEWAY, cause);
  }
}

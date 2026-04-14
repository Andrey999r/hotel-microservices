package com.andrey999r.staynova.general.exceptions.custom;

import com.andrey999r.staynova.general.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ProviderException extends BaseException {

  public ProviderException(String message, Throwable cause) {
    super("Payment provider error: " + message, HttpStatus.BAD_GATEWAY, cause);
  }
}

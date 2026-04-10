package com.exceptions.custom;

import com.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PayerNotFoundException extends BaseException {
  public PayerNotFoundException(String login) {
    super("No payer with login" + login + " found", HttpStatus.NOT_FOUND);
  }
}

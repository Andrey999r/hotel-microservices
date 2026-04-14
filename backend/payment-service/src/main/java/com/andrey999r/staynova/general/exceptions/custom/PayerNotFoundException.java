package com.andrey999r.staynova.general.exceptions.custom;

import com.andrey999r.staynova.general.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PayerNotFoundException extends BaseException {
  public PayerNotFoundException(String login) {
    super("No payer with login" + login + " found", HttpStatus.NOT_FOUND);
  }
}

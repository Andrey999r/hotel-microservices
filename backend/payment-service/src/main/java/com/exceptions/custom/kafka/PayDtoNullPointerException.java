package com.exceptions.custom.kafka;

import com.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PayDtoNullPointerException extends BaseException {
  public PayDtoNullPointerException() {
    super("Empty paydto", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

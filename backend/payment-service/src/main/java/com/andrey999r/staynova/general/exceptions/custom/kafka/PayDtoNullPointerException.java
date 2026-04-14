package com.andrey999r.staynova.general.exceptions.custom.kafka;

import com.andrey999r.staynova.general.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PayDtoNullPointerException extends BaseException {
  public PayDtoNullPointerException() {
    super("Empty paydto", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

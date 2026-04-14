package com.andrey999r.staynova.general.exceptions.custom;

import com.andrey999r.staynova.general.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends BaseException {

  public PaymentNotFoundException(String sessionId) {
    super("Payment not found for provider session: " + sessionId, HttpStatus.NOT_FOUND);
  }
}

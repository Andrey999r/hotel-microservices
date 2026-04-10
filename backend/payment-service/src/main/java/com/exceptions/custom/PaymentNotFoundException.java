package com.exceptions.custom;

import com.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends BaseException {

  public PaymentNotFoundException(String sessionId) {
    super("Payment not found for provider session: " + sessionId, HttpStatus.NOT_FOUND);
  }
}

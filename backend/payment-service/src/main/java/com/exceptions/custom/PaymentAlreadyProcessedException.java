package com.exceptions.custom;

import com.domain.PaymentStatus;
import com.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentAlreadyProcessedException extends BaseException {

  public PaymentAlreadyProcessedException(Long paymentId, PaymentStatus currentStatus) {
    super(
        "Payment id=" + paymentId + " is already processed with status: " + currentStatus,
        HttpStatus.CONFLICT);
  }
}

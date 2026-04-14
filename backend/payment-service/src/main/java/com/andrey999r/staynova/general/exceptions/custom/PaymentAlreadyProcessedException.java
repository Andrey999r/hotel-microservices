package com.andrey999r.staynova.general.exceptions.custom;

import com.andrey999r.staynova.dal.PaymentStatus;
import com.andrey999r.staynova.general.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentAlreadyProcessedException extends BaseException {

  public PaymentAlreadyProcessedException(Long paymentId, PaymentStatus currentStatus) {
    super(
        "Payment id=" + paymentId + " is already processed with status: " + currentStatus,
        HttpStatus.CONFLICT);
  }
}

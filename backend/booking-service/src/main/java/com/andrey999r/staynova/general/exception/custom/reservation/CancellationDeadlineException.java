package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CancellationDeadlineException extends BaseException {

  public CancellationDeadlineException() {
    super(
        "Cancellation is not allowed: reservation can only be cancelled at least 2 days before the check-in date",
        HttpStatus.UNPROCESSABLE_ENTITY);
  }
}

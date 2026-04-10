package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationNotPendingException extends BaseException {
  public ReservationNotPendingException() {
    super("Operation is not allowed: reservation is not in PENDING status", HttpStatus.CONFLICT);
  }
}

package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationConflictException extends BaseException {
  public ReservationConflictException() {
    super(
        "Reservation conflicts with an already approved booking for this room",
        HttpStatus.CONFLICT);
  }
}

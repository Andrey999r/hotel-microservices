package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationAlreadyCancelledException extends BaseException {
  public ReservationAlreadyCancelledException(Long id) {
    super("Reservation " + id + " is already cancelled", HttpStatus.CONFLICT);
  }
}

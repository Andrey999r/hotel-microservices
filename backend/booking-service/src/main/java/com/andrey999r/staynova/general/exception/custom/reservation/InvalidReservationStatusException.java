package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidReservationStatusException extends BaseException {
  public InvalidReservationStatusException() {
    super(
        "Reservation status must not be provided when creating a reservation",
        HttpStatus.BAD_REQUEST);
  }
}

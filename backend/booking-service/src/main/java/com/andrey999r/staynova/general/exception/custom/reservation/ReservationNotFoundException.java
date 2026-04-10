package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationNotFoundException extends BaseException {
  public ReservationNotFoundException(Long id) {
    super("Reservation not found with id: " + id, HttpStatus.NOT_FOUND);
  }
}

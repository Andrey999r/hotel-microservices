package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReservationAccessDeniedException extends BaseException {
  public ReservationAccessDeniedException() {
    super("Access denied: this reservation does not belong to you", HttpStatus.FORBIDDEN);
  }
}

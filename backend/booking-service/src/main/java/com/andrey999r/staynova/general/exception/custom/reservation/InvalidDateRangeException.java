package com.andrey999r.staynova.general.exception.custom.reservation;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidDateRangeException extends BaseException {
  public InvalidDateRangeException() {
    super("Start date must be before end date", HttpStatus.BAD_REQUEST);
  }
}

package com.andrey999r.staynova.general.exception.custom.staynova;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidHotelIdException extends BaseException {
  public InvalidHotelIdException() {
    super("Hotel id must be greater than 0", HttpStatus.BAD_REQUEST);
  }
}

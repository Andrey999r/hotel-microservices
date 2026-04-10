package com.andrey999r.staynova.general.exception.custom.availability;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NoAvailableRoomsException extends BaseException {
  public NoAvailableRoomsException() {
    super("No available rooms found", HttpStatus.NOT_FOUND);
  }
}

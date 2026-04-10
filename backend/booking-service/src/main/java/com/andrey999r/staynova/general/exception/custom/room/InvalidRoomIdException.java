package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRoomIdException extends BaseException {
  public InvalidRoomIdException() {
    super("Room ID must be greater than 0", HttpStatus.BAD_REQUEST);
  }
}

package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmptyRoomListException extends BaseException {
  public EmptyRoomListException() {
    super("Room list must not be null or empty", HttpStatus.BAD_REQUEST);
  }
}

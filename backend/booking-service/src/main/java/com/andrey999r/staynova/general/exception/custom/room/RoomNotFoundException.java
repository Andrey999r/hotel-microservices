package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoomNotFoundException extends BaseException {
  public RoomNotFoundException(Long id) {
    super("Room not found with id: " + id, HttpStatus.NOT_FOUND);
  }
}

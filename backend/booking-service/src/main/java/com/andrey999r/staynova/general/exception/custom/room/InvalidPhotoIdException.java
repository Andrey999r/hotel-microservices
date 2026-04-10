package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPhotoIdException extends BaseException {
  public InvalidPhotoIdException() {
    super("Invalid photo ID", HttpStatus.BAD_REQUEST);
  }
}

package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmptyFileException extends BaseException {
  public EmptyFileException() {
    super("Uploaded file must not be empty", HttpStatus.BAD_REQUEST);
  }
}

package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidSearchQueryException extends BaseException {
  public InvalidSearchQueryException() {
    super("Search query must be non-empty and shorter than 200 characters", HttpStatus.BAD_REQUEST);
  }
}
